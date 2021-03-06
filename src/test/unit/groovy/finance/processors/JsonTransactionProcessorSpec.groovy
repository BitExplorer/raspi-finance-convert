package finance.processors


import com.fasterxml.jackson.databind.ObjectMapper
import finance.domain.Transaction
import org.apache.camel.Exchange
import org.apache.camel.Message
import spock.lang.Specification

import javax.validation.Validator

class JsonTransactionProcessorSpec extends Specification {

    Validator mockValidator = GroovyMock(Validator)
    JsonTransactionProcessor processor = new JsonTransactionProcessor(mockValidator)

    String payload = "[{\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountId\":0,\"accountType\":\"credit\",\"accountNameOwner\":\"amex_brian\",\"transactionDate\":1475647200000,\"description\":\"target.com\",\"category\":\"online\",\"amount\":33.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}," +
            "{\"transactionId\":0,\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountId\":0,\"accountType\":\"credit\",\"accountNameOwner\":\"amex_brian\",\"transactionDate\":1475647200000,\"description\":\"Cafe Roale\",\"category\":\"restaurant\",\"amount\":3.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}]"

    String payloadInvalid = "[{\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountId\":0,\"accountType\":\"credit\",\"accountNameOwner\":\"invalid.invalid\",\"transactionDate\":1475647200000,\"description\":\"target.com\",\"category\":\"online\",\"amount\":33.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}," +
            "{\"transactionId\":0,\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountId\":0,\"accountType\":\"credit\",\"accountNameOwner\":\"amex_brian\",\"transactionDate\":1475647200000,\"description\":\"Cafe Roale\",\"category\":\"restaurant\",\"amount\":3.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}]"


    def "test JsonTransactionProcessor - process - valid records"() {
        Exchange mockExchange = Mock(Exchange)
        Message mockMessage = Mock(Message)
        ObjectMapper mapper = new ObjectMapper()

        given:
        List<Transaction> transactions = mapper.readValue(payload, Transaction[].class)
        println transactions.getClass()
        println transactions.size()

        when:
        processor.process(mockExchange)

        then:
        1 * mockExchange.getIn() >> mockMessage
        1 * mockMessage.getBody(String.class) >> payload
        2 * mockValidator.validate(_) >> new HashSet()
        1 * mockMessage.setBody({
            it ->
                println(it.size())
                println(it.size() == 1)
                println(it.size() == 2)
                println "transactions.getClass()=${transactions.getClass()}"
                return it.size() == 1
        }
        )
        //1 * message.setBody((Object)transactions)
        0 * _
    }


    def "test JsonTransactionProcessor - process - invalid records"() {
        Exchange mockExchange = Mock(Exchange)
        Message mockMessage = Mock(Message)
        ObjectMapper mapper = new ObjectMapper()

        given:
        List<Transaction> transactions = mapper.readValue(payload, Transaction[].class)

        when:
        processor.process(mockExchange)

        then:
        1 * mockExchange.getIn() >> mockMessage
        1 * mockMessage.getBody(String.class) >> payloadInvalid
        //TODO: fix this to reflect the exact message
        2 * mockValidator.validate(_) >> new HashSet()
        1 * mockMessage.setBody({
            it ->
                println(it.size())
                println(it.size() == 1)
                println(it.size() == 2)
                println "transactions.getClass()=${transactions.getClass()}"
                return it.size() == 1
        }
        )
        0 * _
    }

}
