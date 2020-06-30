package finance.services

import com.fasterxml.jackson.databind.ObjectMapper
import finance.configs.CustomProperties
import finance.domain.AccountType
import finance.domain.Transaction
import org.apache.poi.poifs.crypt.Decryptor
import org.apache.poi.poifs.crypt.EncryptionInfo
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Date
import java.sql.Timestamp
import java.util.*
import java.util.stream.IntStream

@Service
class ExcelFileService @Autowired constructor(private val customProperties: CustomProperties) {
    @Throws(Exception::class)
    fun processProtectedExcelFile(inputExcelFileName: String) {
        val fs = POIFSFileSystem(FileInputStream(inputExcelFileName))
        val info = EncryptionInfo(fs)
        val decryptor = Decryptor.getInstance(info)
        decryptor.verifyPassword(customProperties.excelPassword)
        val inputStream = decryptor.getDataStream(fs)
        val workbook: Workbook = XSSFWorkbook(inputStream)
        IntStream.range(0, workbook.numberOfSheets).filter { idx: Int -> (workbook.getSheetName(idx).contains("_brian") || workbook.getSheetName(idx).contains("_kari")) && !workbook.isSheetHidden(idx) }.forEach { idx: Int ->
            if (!isExcludedAccount(customProperties.excludedAccounts, workbook.getSheetName(idx))) {
                logger.info("Sheet name: " + workbook.getSheetName(idx).trim { it <= ' ' })
                val transactionList = processExcelSheet(workbook, idx)
                mapper.writeValue(File(customProperties.jsonInputFilePath + "/" + workbook.getSheetName(idx) + ".json"), transactionList)
            }
        }
        inputStream.close()
    }

    @Throws(IOException::class)
    private fun processExcelSheet(workbook: Workbook, sheetNumber: Int): List<Transaction> {
        val datatypeSheet = workbook.getSheetAt(sheetNumber)
        var blank = false
        val transactionList: MutableList<Transaction> = ArrayList()

        traverseEachWorksheet(datatypeSheet, workbook, sheetNumber, blank, transactionList)
        return transactionList
    }

    private fun traverseEachWorksheet(datatypeSheet: Sheet, workbook: Workbook, sheetNumber: Int, blank: Boolean, transactionList: MutableList<Transaction>) {
        var blank1 = blank
        for (currentRow in datatypeSheet) {
            val tz = TimeZone.getTimeZone(customProperties.timeZone)
            val transaction = Transaction()
            transaction.accountNameOwner = workbook.getSheetName(sheetNumber).trim { it <= ' ' }.replace(".", "-")
            transaction.accountType = getAccountType(customProperties.creditAccounts, workbook.getSheetName(sheetNumber).trim { it <= ' ' })
            blank1 = traverseEachRow(currentRow, blank1, transaction, tz)
            if (!blank1) {
                if (transaction.guid.isNotEmpty()) {
                    transactionList.add(transaction)
                }
            }
        }
    }

    private fun traverseEachRow(currentRow: Row, blank: Boolean, transaction: Transaction, tz: TimeZone?): Boolean {
        var blank1 = blank
        for (currentCell in currentRow) {
            val col = currentCell.columnIndex
            blank1 = false
            if (col == COL_GUID && currentCell.stringCellValue.trim { it <= ' ' } == "") {
                blank1 = true
                break
            }
            loadColumns(currentCell, col, transaction, tz)
        }
        return blank1
    }

    private fun loadColumns(currentCell: Cell, col: Int, transaction: Transaction, tz: TimeZone?) {
        if (currentCell.address.row != 0) {
            if (currentCell.cellType == CellType.STRING) {
                loadStringValueColumns(col, currentCell, transaction)
            } else if (currentCell.cellType == CellType.NUMERIC) {
                loadNumericValueColumns(col, currentCell, transaction, tz)
            } else if (currentCell.cellType == CellType.BLANK) {
                if (col != COL_TRANSACTION_ID) {
                    logger.info("blank: $col")
                }
            } else if (currentCell.cellType == CellType.FORMULA) {
                logger.warn(transaction.guid)
                logger.info("formula needs to be changed to the actual value.")
                throw RuntimeException("formula needs to be changed to the actual value.")
            } else {
                logger.info("currentCell.getCellType()=" + currentCell.cellType)
                throw RuntimeException("currentCell.getCellType()=" + currentCell.cellType)
            }
        }
    }

    private fun loadNumericValueColumns(col: Int, currentCell: Cell, transaction: Transaction, tz: TimeZone?) {
        when (col) {
            COL_CLEARED -> {
                val `val` = currentCell.numericCellValue.toInt()
                transaction.cleared = `val`
            }
            COL_DATE_UPDATED -> {
                val date = DateUtil.getJavaDate(currentCell.numericCellValue, tz)
                transaction.dateUpdated = Timestamp(date.time)
            }
            COL_DATE_ADDED -> {
                val date = DateUtil.getJavaDate(currentCell.numericCellValue, tz)
                transaction.dateAdded = Timestamp(date.time)
            }
            COL_TRANSACTION_DATE -> {
                val date = DateUtil.getJavaDate(currentCell.numericCellValue, tz)
                transaction.transactionDate = Date(date.time)
            }
            COL_AMOUNT -> {
                val `val` = BigDecimal.valueOf(currentCell.numericCellValue)
                val displayVal = `val`.setScale(2, RoundingMode.HALF_EVEN)
                transaction.amount = displayVal
            }
            else -> {
                logger.warn("currentCell.getCellType()=" + currentCell.cellType)
                throw RuntimeException("currentCell.getCellType()=" + currentCell.cellType)
            }
        }
    }

    private fun loadStringValueColumns(col: Int, currentCell: Cell, transaction: Transaction) {
        when (col) {
            COL_GUID -> {
                val `val` = currentCell.stringCellValue.trim { it <= ' ' }
                transaction.guid = `val`
            }
            COL_DESCRIPTION -> {
                val `val` = capitalizeWords(currentCell.stringCellValue.trim { it <= ' ' })
                transaction.description = `val`
            }
            COL_CATEGORY -> {
                val `val` = currentCell.stringCellValue.trim { it <= ' ' }
                transaction.category = `val`
            }
            COL_NOTES -> {
                val `val` = capitalizeWords(currentCell.stringCellValue.trim { it <= ' ' })
                transaction.notes = `val`
                transaction.reoccurring = `val`.startsWith("reoccur")
            }
            else -> {
                logger.warn("currentCell.getCellType()=" + currentCell.cellType)
                throw RuntimeException("currentCell.getCellType()=" + currentCell.cellType)
            }
        }
    }

    private fun capitalizeWords(str: String): String {
        if (str.isNotEmpty()) {
            val words = str.toLowerCase().split("\\s").toTypedArray()
            val capitalizeWord = StringBuilder()
            for (w in words) {
                if (w.isNotEmpty()) {
                    val first = w.substring(0, 1)
                    val afterFirst = w.substring(1)
                    capitalizeWord.append(first.toUpperCase()).append(afterFirst).append(" ")
                } else {
                    capitalizeWord.append(w)
                }
            }
            return capitalizeWord.toString().trim { it <= ' ' }
        }
        return str
    }

    private fun isExcludedAccount(accountExcludedList: List<String>, accountNameOwner: String): Boolean {
        return accountExcludedList.stream().anyMatch { str: String -> str.trim { it <= ' ' } == accountNameOwner }
    }

    private fun getAccountType(accountCreditList: List<String>, accountNameOwner: String): AccountType {
        return if (accountCreditList.stream().anyMatch { str: String -> str.trim { it <= ' ' } == accountNameOwner }) {
            AccountType.Credit
        } else AccountType.Debit
    }

    companion object {
        const val COL_TRANSACTION_ID = 0
        const val COL_GUID = 1
        const val COL_TRANSACTION_DATE = 2
        const val COL_DESCRIPTION = 3
        const val COL_CATEGORY = 4
        const val COL_AMOUNT = 5
        const val COL_CLEARED = 6
        const val COL_NOTES = 7
        const val COL_DATE_ADDED = 8
        const val COL_DATE_UPDATED = 9
        val mapper = ObjectMapper()
        val logger: Logger
            get() = LoggerFactory.getLogger(ExcelFileService::class.java)
    }
}
