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
    TransactionRepository mockTransactionRepository = Mock(TransactionRepository)
    MeterService mockMeterService = Mock(MeterService)
    AccountRepository mockAccountRepository = Mock(AccountRepository)
    AccountService accountService = new AccountService(mockAccountRepository, mockMeterService)
    CategoryRepository mockCategoryRepository = Mock(CategoryRepository)
    CategoryService categoryService = new CategoryService(mockCategoryRepository, mockMeterService)
    Validator validator = Mock(Validator)

    TransactionService service = new TransactionService(mockTransactionRepository, accountService, categoryService, validator, mockMeterService)

    def "test findByGuid returns a transaction"() {

        given:
        Transaction transaction = TransactionBuilder.builder().build()
        def transactionOptional = Optional.of(TransactionBuilder.builder().build())

        when: "findByGuid is called"
        def result = service.findByGuid(transaction.guid)

        then:
        result.is(transactionOptional)
        1 * mockTransactionRepository.findByGuid(transaction.guid) >> transactionOptional
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
        1 * mockTransactionRepository.findByGuid(transaction.guid) >> transactionOptional
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
        1 * mockTransactionRepository.findByGuid(transaction.guid) >> Optional.empty()
        1 * mockAccountRepository.findByAccountNameOwner(transaction.accountNameOwner) >> Optional.of(account)
        1 * mockCategoryRepository.findByCategory(transaction.category) >> Optional.of(category)
        1 * mockTransactionRepository.saveAndFlush(transaction) >> true
        1 * mockMeterService.incrementTransactionReceivedCounter(transaction.accountNameOwner)
        1 * mockMeterService.incrementTransactionSuccessfullyInsertedCounter(transaction.accountNameOwner)
        0 * _
    }


    def "test insert valid transaction - account name does not exist"() {
        given:
        def categoryName = "my-category"
        def accountName = "my-account-name"
        def guid = "123"
        Transaction transaction = new Transaction()
        Account account = new Account()
        Category category = new Category()
        Optional<Account> accountOptional = Optional.of(account)
        Optional<Category> categoryOptional = Optional.of(category)
        when:
        transaction.guid = guid
        transaction.accountNameOwner = accountName
        transaction.category = categoryName
        def isInserted = service.insertTransaction(transaction)
        then:
        isInserted.is(true)
        1 * mockTransactionRepository.findByGuid(guid) >> Optional.empty()
        1 * mockAccountRepository.findByAccountNameOwner(accountName) >> Optional.empty()
        1 * mockAccountRepository.saveAndFlush(_) >> true
        1 * mockAccountRepository.findByAccountNameOwner(accountName) >> accountOptional
        1 * mockCategoryRepository.findByCategory(categoryName) >> categoryOptional
        1 * mockTransactionRepository.saveAndFlush(transaction) >> true
        1 * mockMeterService.incrementTransactionReceivedCounter(accountName)
        1 * validator.validate(transaction) >> new HashSet()
        1 * mockMeterService.incrementTransactionSuccessfullyInsertedCounter(accountName)
        0 * _
    }

}
