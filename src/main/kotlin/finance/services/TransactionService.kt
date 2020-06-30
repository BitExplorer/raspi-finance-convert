package finance.services

import finance.domain.Account
import finance.domain.Category
import finance.domain.Transaction
import finance.domain.AccountType
import finance.repositories.TransactionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.Optional
import java.util.Optional.empty
import javax.validation.ConstraintViolation
import javax.validation.Validator

@Service
class TransactionService @Autowired constructor(
        private val transactionRepository: TransactionRepository,
        private val accountService: AccountService,
        private val categoryService: CategoryService,
        private val validator: Validator,
        private val meterService: MeterService
) {
    //@Transactional
    //@Timed(value = "insert.transaction.timer")
    fun insertTransaction(transaction: Transaction): Boolean {
        logger.info("*** insert transaction ***")
        meterService.incrementTransactionReceivedCounter(transaction.accountNameOwner)
        val transactionOptional = findByGuid(transaction.guid)

        val constraintViolations: Set<ConstraintViolation<Transaction>> = validator.validate(transaction)
        if (constraintViolations.isNotEmpty()) {
            //TODO: handle the violation

            logger.info("validation issue for<${transaction}>")
            meterService.incrementErrorCounter(transaction.accountNameOwner, MeterService.ErrorType.VALIDATION_ERROR)
            logger.info("METRIC_TRANSACTION_VALIDATOR_FAILED_COUNTER")
        }

        if (transactionOptional.isPresent) {
            val transactionDb = transactionOptional.get()
            logger.info("*** update transaction ***")
            return updateTransaction(transactionDb, transaction)
        }

        processAccount(transaction)
        processCategory(transaction)
        transactionRepository.saveAndFlush(transaction)
        logger.info("*** inserted transaction ***")
        meterService.incrementTransactionSuccessfullyInsertedCounter(transaction.accountNameOwner)
        return true
    }

    private fun processAccount(transaction: Transaction) {
        var accountOptional = accountService.findByAccountNameOwner(transaction.accountNameOwner)
        if (accountOptional.isPresent) {
            logger.info("METRIC_ACCOUNT_ALREADY_EXISTS_COUNTER")
            transaction.accountId = accountOptional.get().accountId
            //meterRegistry.counter(METRIC_ACCOUNT_ALREADY_EXISTS_COUNTER).increment()
        } else {
            logger.info("METRIC_ACCOUNT_NOT_FOUND_COUNTER")
            val account = createDefaultAccount(transaction.accountNameOwner, transaction.accountType)
            logger.debug("will insertAccount")
            accountService.insertAccount(account)
            logger.debug("called insertAccount")
            accountOptional = accountService.findByAccountNameOwner(transaction.accountNameOwner)
            transaction.accountId = accountOptional.get().accountId
            //meterRegistry.counter(METRIC_ACCOUNT_NOT_FOUND_COUNTER).increment()
        }
    }

    private fun processCategory(transaction: Transaction) {
        when {
            transaction.category != "" -> {
                val optionalCategory = categoryService.findByCategory(transaction.category)
                if (optionalCategory.isPresent) {
                    transaction.categories.add(optionalCategory.get())
                } else {
                    val category = createDefaultCategory(transaction.category)
                    categoryService.insertCategory(category)
                    transaction.categories.add(category)
                }
            }
        }
    }

    //@Timed("find.by.guid.timer")
    fun findByGuid(guid: String): Optional<Transaction> {
        val transactionOptional: Optional<Transaction> = transactionRepository.findByGuid(guid)
        if (transactionOptional.isPresent) {
            return transactionOptional
        }
        return empty()
    }

    private fun createDefaultCategory(categoryName: String): Category {
        val category = Category()

        category.category = categoryName
        return category
    }

    private fun createDefaultAccount(accountNameOwner: String, accountType: AccountType): Account {
        val account = Account()

        account.accountNameOwner = accountNameOwner
        account.moniker = "0000"
        account.accountType = accountType
        account.activeStatus = true
        account.dateAdded = Timestamp(System.currentTimeMillis())
        account.dateUpdated = Timestamp(System.currentTimeMillis())
        return account
    }

    private fun updateTransaction(transactionDb: Transaction, transaction: Transaction): Boolean {
        if(transactionDb.accountNameOwner.trim() == transaction.accountNameOwner) {

            if( transactionDb.amount != transaction.amount ) {
                logger.info("discrepancy in the amount for <${transactionDb.guid}>")
                //TODO: metric for this
                transactionRepository.setAmountByGuid(transaction.amount, transaction.guid)
                return true
            }

            if( transactionDb.cleared != transaction.cleared ) {
                meterService.incrementTransactionUpdateClearedCounter(transaction.accountNameOwner)
                logger.info("discrepancy in the cleared value for <${transactionDb.guid}>")
                transactionRepository.setClearedByGuid(transaction.cleared, transaction.guid)
                return true
            }
        }

        logger.info("transaction already exists, no transaction data inserted.")
        meterService.incrementTransactionAlreadyExistsCounter(transaction.accountNameOwner)

        return false
    }

    companion object {
        val logger : Logger
            get() = LoggerFactory.getLogger(TransactionService::class.java)
    }
}
