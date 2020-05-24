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
    Message message = Mock(Message)
    Exchange exchange = Mock(Exchange)
    MeterService meterService = Mock(MeterService)
    TransactionRepository transactionRepository = Mock(TransactionRepository)
    AccountRepository accountRepository = Mock(AccountRepository)
    AccountService accountService = new AccountService(accountRepository, meterService)
    CategoryRepository categoryRepository = Mock(CategoryRepository)
    CategoryService categoryService = new CategoryService(categoryRepository, meterService)
    Validator validator = Mock(Validator)
    ObjectMapper mapper = new ObjectMapper()

    TransactionService transactionService = new TransactionService(transactionRepository, accountService, categoryService, validator, meterService)
    InsertTransactionProcessor processor = new InsertTransactionProcessor(transactionService)

    def "test InsertTransactionProcessor" () {
        String payload = "{\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountType\":\"credit\",\"accountNameOwner\":\"amex_brian\",\"transactionDate\":1475647200000,\"description\":\"Cafe Roale\",\"category\":\"online\",\"amount\":33.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}"
        //String guid = "0a23fec3-18c8-4b89-a5af-68fab8db8620"

        given:
        Transaction transaction = mapper.readValue(payload, Transaction.class)

        and:
        def guid = transaction.guid

        when:
        processor.process(exchange)

        then:
        1 * exchange.getIn() >> message
        1 * message.getBody(String.class) >> payload
        //1 * meterRegistry.timer('insert.transaction.timer', []) >> timer
        //1 * timer.record(_ as Object)
        1 * meterService.incrementTransactionReceivedCounter(transaction.accountNameOwner)
        1 * meterService.incrementTransactionAlreadyExistsCounter(transaction.accountNameOwner)
        1 * transactionRepository.findByGuid(guid) >> Optional.of(transaction)
        1 * validator.validate(_ as Object) >> new HashSet()
        1 * message.setBody(_ as Object)
        0 * _
    }
}
