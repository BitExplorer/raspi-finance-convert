package finance.processors


import com.fasterxml.jackson.databind.ObjectMapper
import finance.domain.Transaction
import org.apache.camel.Exchange
import org.apache.camel.Message
import spock.lang.Specification

class JsonTransactionProcessorSpec extends Specification {

    //private static ObjectMapper mapper1 = new ObjectMapper()

    def "test JsonTransactionProcessor"() {
        Exchange mockExchange = Mock(Exchange)
        Message mockMessage = Mock(Message)
        ObjectMapper mapper = new ObjectMapper()

        String payload = "[{\"transactionId\":0,\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountId\":0,\"accountType\":\"credit\",\"accountNameOwner\":\"amex_brian\",\"transactionDate\":1475647200000,\"description\":\"target.com\",\"category\":\"online\",\"amount\":33.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}," +
                "{\"transactionId\":0,\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountId\":0,\"accountType\":\"credit\",\"accountNameOwner\":\"amex_brian\",\"transactionDate\":1475647200000,\"description\":\"Cafe Roale\",\"category\":\"restaurant\",\"amount\":3.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}]"
        //List<Transaction> transactions = new ArrayList<>()

        given:
        List<Transaction> transactions = mapper.readValue(payload, Transaction[].class)
        println transactions.getClass()
        println transactions.size()

        and:
        JsonTransactionProcessor processor = new JsonTransactionProcessor()

        when:
        processor.process(mockExchange)

        then:
        1 * mockExchange.getIn() >> mockMessage
        1 * mockMessage.getBody(String.class) >> payload
        //1 * message.setBody(_ as Object)
        //1 * message.setBody({it -> return it.size() == 2})
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
}
