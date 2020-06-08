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
import org.apache.camel.Exchange
import org.apache.camel.Message
import spock.lang.Specification
import javax.validation.Validator

class InsertTransactionProcessorSpec extends Specification {
    Message mockMessage = Mock(Message)
    Exchange mockExchange = Mock(Exchange)
    MeterService mockMeterService = Mock(MeterService)
    TransactionRepository mockTransactionRepository = Mock(TransactionRepository)
    AccountRepository mockAccountRepository = Mock(AccountRepository)
    AccountService accountService = new AccountService(mockAccountRepository, mockMeterService)
    CategoryRepository mockCategoryRepository = Mock(CategoryRepository)
    CategoryService categoryService = new CategoryService(mockCategoryRepository, mockMeterService)
    Validator mockValidator = Mock(Validator)
    ObjectMapper mapper = new ObjectMapper()

    TransactionService transactionService = new TransactionService(mockTransactionRepository, accountService, categoryService, mockValidator, mockMeterService)
    InsertTransactionProcessor processor = new InsertTransactionProcessor(transactionService)

    def "test InsertTransactionProcessor" () {
        String payload = "{\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountType\":\"credit\",\"accountNameOwner\":\"amex_brian\",\"transactionDate\":1475647200000,\"description\":\"Cafe Roale\",\"category\":\"online\",\"amount\":33.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}"
        //String guid = "0a23fec3-18c8-4b89-a5af-68fab8db8620"

        given:
        Transaction transaction = mapper.readValue(payload, Transaction.class)

        and:
        def guid = transaction.guid

        when:
        processor.process(mockExchange)

        then:
        1 * mockExchange.getIn() >> mockMessage
        1 * mockMessage.getBody(String.class) >> payload
        //1 * meterRegistry.timer('insert.transaction.timer', []) >> timer
        //1 * timer.record(_ as Object)
        1 * mockMeterService.incrementTransactionReceivedCounter(transaction.accountNameOwner)
        1 * mockMeterService.incrementTransactionAlreadyExistsCounter(transaction.accountNameOwner)
        1 * mockTransactionRepository.findByGuid(guid) >> Optional.of(transaction)
        1 * mockValidator.validate(_ as Object) >> new HashSet()
        1 * mockMessage.setBody(_ as Object)
        0 * _
    }
}
