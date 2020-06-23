package finance.processors

import com.fasterxml.jackson.databind.ObjectMapper
import finance.domain.Transaction
import finance.repositories.AccountRepository
import finance.repositories.CategoryRepository
import finance.repositories.TransactionRepository
import finance.services.AccountService
import finance.services.CategoryService
import finance.services.MeterService
import finance.services.TransactionService
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.apache.camel.Exchange
import org.apache.camel.Message
import spock.lang.Specification
import javax.validation.Validator

class InsertTransactionProcessorSpec extends Specification {
    Message mockMessage = GroovyMock(Message)
    Exchange mockExchange = GroovyMock(Exchange)
    MeterRegistry mockMeterRegistry = GroovyMock(MeterRegistry)
    MeterService meterService = new MeterService(mockMeterRegistry)
    TransactionRepository mockTransactionRepository = GroovyMock(TransactionRepository)
    AccountRepository mockAccountRepository = GroovyMock(AccountRepository)
    AccountService accountService = new AccountService(mockAccountRepository, meterService)
    CategoryRepository mockCategoryRepository = GroovyMock(CategoryRepository)
    CategoryService categoryService = new CategoryService(mockCategoryRepository, meterService)
    Validator mockValidator = GroovyMock(Validator)
    ObjectMapper mapper = new ObjectMapper()
    Counter mockCounter = GroovyMock(Counter)

    TransactionService transactionService = new TransactionService(mockTransactionRepository, accountService, categoryService, mockValidator, meterService)
    InsertTransactionProcessor processor = new InsertTransactionProcessor(transactionService)

    def "test InsertTransactionProcessor" () {
        String payload = "{\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountType\":\"credit\",\"accountNameOwner\":\"amex_brian\",\"transactionDate\":1475647200000,\"description\":\"Cafe Roale\",\"category\":\"online\",\"amount\":33.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}"

        given:
        Transaction transaction = mapper.readValue(payload, Transaction.class)

        and:
        def guid = transaction.guid

        when:
        processor.process(mockExchange)

        then:
        1 * mockExchange.getIn() >> mockMessage
        1 * mockMessage.getBody(String.class) >> payload
        1 * mockTransactionRepository.findByGuid(guid) >> Optional.of(transaction)
        1 * mockValidator.validate(_ as Object) >> new HashSet()
        1 * mockMessage.setBody(_ as Object)
        1 * mockMeterRegistry.counter('transaction.received.event.counter', ['account.name', 'amex_brian']) >> mockCounter
        1 * mockMeterRegistry.counter('transaction.already.exists.counter', ['account.name', 'amex_brian']) >> mockCounter
        2 * mockCounter.increment()
        0 * _
    }
}
