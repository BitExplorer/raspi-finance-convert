package finance.services

import finance.domain.Account
import finance.domain.Transaction
import finance.domain.Category
import finance.repositories.AccountRepository
import finance.repositories.CategoryRepository
import finance.repositories.TransactionRepository
import finance.helpers.AccountBuilder
import finance.helpers.TransactionBuilder
import spock.lang.Specification

import javax.validation.Validator

class TransactionServiceSpec extends Specification {
    TransactionRepository transactionRepository = Mock(TransactionRepository)
    MeterService meterService = Mock(MeterService)
    AccountRepository accountRepository = Mock(AccountRepository)
    AccountService accountService = new AccountService(accountRepository, meterService)
    CategoryRepository categoryRepository = Mock(CategoryRepository)
    CategoryService categoryService = new CategoryService(categoryRepository, meterService)
    Validator validator = Mock(Validator)

    TransactionService service = new TransactionService(transactionRepository, accountService, categoryService, validator, meterService)

    def "test findByGuid returns a transaction"() {

        given:
        Transaction transaction = TransactionBuilder.builder().build()
        def transactionOptional = Optional.of(TransactionBuilder.builder().build())

        when: "findByGuid is called"
        def result = service.findByGuid(transaction.guid)

        then:
        result.is(transactionOptional)
        1 * transactionRepository.findByGuid(transaction.guid) >> transactionOptional
        0 * _
    }

    def "test findByGuid that returns an empty transaction"() {

        given:
        def transactionOptional = Optional.empty()
        Transaction transaction = TransactionBuilder.builder().build()

        when: "findByGuid is called"
        def result = service.findByGuid(transaction.guid)

        then:
        result.is(transactionOptional)
        1 * transactionRepository.findByGuid(transaction.guid) >> transactionOptional
        0 * _
    }

    //@PendingFeature
    def "test new insertTransaction"() {
        Category category = new Category()
        category.category = "online"

        given:
        Transaction transaction = TransactionBuilder.builder().build()
        Account account = AccountBuilder.builder().build()

        when: "insertTransaction is called"
        def result = service.insertTransaction(transaction)

        then:
        result.is(true)
        1 * validator.validate(transaction) >> new HashSet()
        1 * transactionRepository.findByGuid(transaction.guid) >> Optional.empty()
        1 * accountRepository.findByAccountNameOwner(transaction.accountNameOwner) >> Optional.of(account)
        1 * categoryRepository.findByCategory(transaction.category) >> Optional.of(category)
        1 * transactionRepository.saveAndFlush(transaction) >> true
        1 * meterService.incrementTransactionReceivedCounter(transaction.accountNameOwner)
        1 * meterService.incrementTransactionSuccessfullyInsertedCounter(transaction.accountNameOwner)
        0 * _
    }
}
