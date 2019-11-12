package finance.services

import finance.models.Account
import finance.models.Category
import finance.models.Transaction
import finance.pojos.AccountType
import finance.repositories.TransactionRepository
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.Optional
import java.util.Optional.empty
import javax.validation.ConstraintViolation
import javax.validation.Validator

@Service
open class TransactionService @Autowired constructor(
        private val transactionRepository: TransactionRepository<Transaction>,
        private val accountService: AccountService,
        private val categoryService: CategoryService,
        private val validator: Validator,
        private val meterRegistry: MeterRegistry
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    //@Transactional
    //@Timed(value = "insert.transaction.timer")
    fun insertTransaction(transaction: Transaction): Boolean {
        logger.debug("insertTransaction")
        val transactionOptional = findByGuid(transaction.guid)

        val constraintViolations: Set<ConstraintViolation<Transaction>> = validator.validate(transaction)
        if (constraintViolations.isNotEmpty()) {
            //TODO: handle the violation

            logger.info("validation issue for<${transaction}>")
            //meterRegistry.counter(METRIC_TRANSACTION_VALIDATOR_FAILED_COUNTER).increment()
            logger.info("METRIC_TRANSACTION_VALIDATOR_FAILED_COUNTER")
        }

        if (transactionOptional.isPresent) {
            val transactionDb = transactionOptional.get()
            
            return updateTransaction(transactionDb, transaction)
        }

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

        when {
            transaction.category != "" -> {
                val optionalCategory = categoryService.findByCategory(transaction.category)
                if (optionalCategory.isPresent) {
                    transaction.categries.add(optionalCategory.get())
                } else {
                    val category = createDefaultCategory(transaction.category)
                    categoryService.insertCategory(category)
                    transaction.categries.add(category)
                }
            }
        }
        logger.debug("will insert transaction")
        transactionRepository.saveAndFlush(transaction)
        //transactionRepository.count()
        logger.debug("inserted transaction")
        //meterRegistry.counter(METRIC_TRANSACTION_DATABASE_INSERT_COUNTER).increment()
        return true
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
                transactionRepository.setAmountByGuid(transaction.amount, transaction.guid)
                return true
            }

            if( transactionDb.cleared != transaction.cleared ) {
                logger.info("discrepancy in the cleared value for <${transactionDb.guid}>")
                transactionRepository.setClearedByGuid(transaction.cleared, transaction.guid)
                return true
            }
        }

        logger.info("transaction already exists, no transaction data inserted.")
        //meterRegistry.counter(METRIC_TRANSACTION_ALREADY_EXISTS_COUNTER).increment()
        logger.info("METRIC_TRANSACTION_ALREADY_EXISTS_COUNTER")

        return false
    }
}
