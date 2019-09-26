package finance.services

import finance.models.Account
import finance.models.Transaction
import finance.models.Category
import finance.repositories.AccountRepository
import finance.repositories.CategoryRepository
import finance.repositories.TransactionRepository
import finance.helpers.AccountBuilder
import finance.helpers.TransactionBuilder
import io.micrometer.core.instrument.MeterRegistry
import spock.lang.PendingFeature
import spock.lang.Specification

import javax.validation.ConstraintViolation
import javax.validation.Validator

class TransactionServiceSpec extends Specification {
    TransactionRepository transactionRepository = Mock(TransactionRepository)
    MeterRegistry meterRegistry = Mock(MeterRegistry)
    AccountRepository accountRepository = Mock(AccountRepository)
    AccountService accountService = new AccountService(accountRepository,meterRegistry)
    CategoryRepository categoryRepository = Mock(CategoryRepository)
    CategoryService categoryService = new CategoryService(categoryRepository, meterRegistry)
    Validator validator = Mock(Validator)

    TransactionService service = new TransactionService(transactionRepository, accountService, categoryService, validator, meterRegistry)

    void setup() {
    }

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

    @PendingFeature
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
        1 * validator.validate(transaction) >> new HashSet<>()
        1 * transactionRepository.findByGuid(transaction.guid) >> Optional.empty()
        1 * accountRepository.findByAccountNameOwner(transaction.accountNameOwner) >> Optional.of(account)
        1 * categoryRepository.findByCategory(transaction.category) >> Optional.of(category)
        1 * transactionRepository.saveAndFlush(transaction) >> true
        //1 * meterRegistry.counter('transaction.record.inserted.count', [])
        0 * _
    }
}
