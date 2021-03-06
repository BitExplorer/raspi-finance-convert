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
import java.io.File
import java.io.FileInputStream
import java.io.IOException
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
        filterWorkbookThenExportSheetsAsJson(workbook)
        inputStream.close()
    }

    private fun filterWorkbookThenExportSheetsAsJson(workbook: Workbook) {
        IntStream.range(0, workbook.numberOfSheets).filter { idx: Int -> (workbook.getSheetName(idx).contains("_brian") || workbook.getSheetName(idx).contains("_kari")) && !workbook.isSheetHidden(idx) }.forEach { idx: Int ->
            if (!isExcludedAccount(customProperties.excludedAccounts, workbook.getSheetName(idx))) {
                exportSheetAsJson(workbook, idx)
            }
        }
    }

    private fun exportSheetAsJson(workbook: Workbook, idx: Int) {
        logger.info("Sheet name: " + workbook.getSheetName(idx).trim { it <= ' ' })
        val transactionList = processExcelSheet(workbook, idx)
        mapper.writeValue(File(customProperties.jsonInputFilePath + "/" + workbook.getSheetName(idx) + ".json"), transactionList)
    }

    @Throws(IOException::class)
    private fun processExcelSheet(workbook: Workbook, sheetNumber: Int): List<Transaction> {
        val datatypeSheet = workbook.getSheetAt(sheetNumber)
        val transactionList: MutableList<Transaction> = ArrayList()

        traverseEachRowInTheWorksheet(datatypeSheet, workbook, sheetNumber,  transactionList)
        return transactionList
    }

    private fun traverseEachRowInTheWorksheet(datatypeSheet: Sheet, workbook: Workbook, sheetNumber: Int, transactionList: MutableList<Transaction>) {
        var isRowBlank: Boolean
        for (currentRow in datatypeSheet) {
            val timezone = TimeZone.getTimeZone(customProperties.timeZone)
            val transaction = Transaction()
            transaction.accountNameOwner = workbook.getSheetName(sheetNumber).trim { it <= ' ' }.replace(".", "-")
            transaction.accountType = getAccountType(customProperties.creditAccounts, workbook.getSheetName(sheetNumber).trim { it <= ' ' })
            isRowBlank = traverseEachColumnOfTheRow(currentRow, transaction, timezone)
            addToTransactionList(isRowBlank, transaction, transactionList)
        }
    }

    private fun addToTransactionList(isRowBlank: Boolean, transaction: Transaction, transactionList: MutableList<Transaction>) {
        if (!isRowBlank && transaction.guid.isNotEmpty()) {
            transactionList.add(transaction)
        }
    }

    private fun traverseEachColumnOfTheRow(currentRow: Row, transaction: Transaction, timezone: TimeZone): Boolean {
        for (currentCell in currentRow) {
            val currentColumnIndex = currentCell.columnIndex
            if (isGuidValueEmpty(currentColumnIndex, currentCell)) {
                return true
            }
            loadColumnValuesIntoTransaction(currentCell, currentColumnIndex, transaction, timezone)
        }
        return false
    }

    private fun isGuidValueEmpty(currentColumnIndex: Int, currentCell: Cell): Boolean {
        if (currentColumnIndex == COL_GUID && currentCell.stringCellValue.trim { it <= ' ' } == "") {
            return true
        }
        return false
    }

    private fun loadColumnValuesIntoTransaction(currentCell: Cell, column: Int, transaction: Transaction, timeZone: TimeZone) {
        if (currentCell.address.row != 0) {
            if (currentCell.cellType == CellType.STRING) {
                loadStringValueColumns(column, currentCell, transaction)
            } else if (currentCell.cellType == CellType.NUMERIC) {
                loadNumericValueColumns(column, currentCell, transaction, timeZone)
            } else if (currentCell.cellType == CellType.BLANK) {
//                if (column != COL_TRANSACTION_ID) {
//                    logger.info("blank: $column")
//                }
            } else if (currentCell.cellType == CellType.FORMULA) {
                logger.info("formula needs to be changed to the actual value: ${transaction.guid}")
                throw RuntimeException("formula needs to be changed to the actual value:  ${transaction.guid}")
            } else {
                logger.info("currentCell.getCellType = ${currentCell.cellType}")
                throw RuntimeException("currentCell.getCellType = ${currentCell.cellType}")
            }
        }
    }

    private fun loadNumericValueColumns(columnNumber: Int, currentCell: Cell, transaction: Transaction, timezone: TimeZone) {
        when (columnNumber) {
            COL_CLEARED -> {
                populateCleared(currentCell, transaction)
            }
            COL_DATE_UPDATED -> {
                populateDateUpdated(currentCell, timezone, transaction)
            }
            COL_DATE_ADDED -> {
                populateDateAdded(currentCell, timezone, transaction)
            }
            COL_TRANSACTION_DATE -> {
                populateTransactionDate(currentCell, timezone, transaction)
            }
            COL_AMOUNT -> {
                populateAmount(currentCell, transaction)
            }
            else -> {
                logger.warn("currentCell.getCellType = ${currentCell.cellType}")
                throw RuntimeException("currentCell.getCellType = ${currentCell.cellType}")
            }
        }
    }

    private fun populateAmount(currentCell: Cell, transaction: Transaction) {
        val `val` = BigDecimal.valueOf(currentCell.numericCellValue)
        val displayVal = `val`.setScale(2, RoundingMode.HALF_EVEN)
        transaction.amount = displayVal
    }

    private fun populateTransactionDate(currentCell: Cell, timezone: TimeZone, transaction: Transaction) {
        val date = DateUtil.getJavaDate(currentCell.numericCellValue, timezone)
        transaction.transactionDate = Date(date.time)
    }

    private fun populateDateAdded(currentCell: Cell, timezone: TimeZone, transaction: Transaction) {
        val date = DateUtil.getJavaDate(currentCell.numericCellValue, timezone)
        transaction.dateAdded = Timestamp(date.time)
    }

    private fun populateDateUpdated(currentCell: Cell, timezone: TimeZone, transaction: Transaction) {
        val date = DateUtil.getJavaDate(currentCell.numericCellValue, timezone)
        transaction.dateUpdated = Timestamp(date.time)
    }

    private fun populateCleared(currentCell: Cell, transaction: Transaction) {
        val `val` = currentCell.numericCellValue.toInt()
        transaction.cleared = `val`
    }

    private fun loadStringValueColumns(columnNumber: Int, currentCell: Cell, transaction: Transaction) {
        when (columnNumber) {
            COL_GUID -> {
                populateGuid(currentCell, transaction)
            }
            COL_DESCRIPTION -> {
                populateDescription(currentCell, transaction)
            }
            COL_CATEGORY -> {
                populateCategory(currentCell, transaction)
            }
            COL_NOTES -> {
                populateNotesAndReoccurring(currentCell, transaction)
            }
            else -> {
                logger.warn("currentCell.getCellType = ${currentCell.cellType}")
                throw RuntimeException("currentCell.getCellType = ${currentCell.cellType}")
            }
        }
    }

    private fun populateNotesAndReoccurring(currentCell: Cell, transaction: Transaction) {
        val `val` = capitalizeWords(currentCell.stringCellValue.trim { it <= ' ' })
        transaction.notes = `val`
        transaction.reoccurring = `val`.toLowerCase().startsWith("reoccur")
    }

    private fun populateCategory(currentCell: Cell, transaction: Transaction) {
        val `val` = currentCell.stringCellValue.trim { it <= ' ' }
        transaction.category = `val`
    }

    private fun populateDescription(currentCell: Cell, transaction: Transaction) {
        val `val` = capitalizeWords(currentCell.stringCellValue.trim { it <= ' ' })
        transaction.description = `val`
    }

    private fun populateGuid(currentCell: Cell, transaction: Transaction) {
        val `val` = currentCell.stringCellValue.trim { it <= ' ' }
        transaction.guid = `val`
    }

    private fun capitalizeWords(inputString: String): String {
        if (inputString.isNotEmpty()) {
            val words = inputString.toLowerCase().split("\\s").toTypedArray()
            val capitalizeWord = StringBuilder()
            for (word in words) {
                if (word.isNotEmpty()) {
                    val first = word.substring(0, 1)
                    val afterFirst = word.substring(1)
                    capitalizeWord.append(first.toUpperCase()).append(afterFirst).append(" ")
                } else {
                    capitalizeWord.append(word)
                }
            }
            return capitalizeWord.toString().trim { it <= ' ' }
        }
        return inputString
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
