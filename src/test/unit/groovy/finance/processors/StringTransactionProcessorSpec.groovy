package finance.processors

import com.fasterxml.jackson.databind.ObjectMapper
import finance.domain.Transaction
import org.apache.camel.Exchange
import org.apache.camel.Message
import spock.lang.Specification

class StringTransactionProcessorSpec extends Specification {
    Exchange mockExchange = Mock(Exchange)
    Message mockMessage = Mock(Message)
    ObjectMapper mapper = new ObjectMapper()

    void "test StringTransactionProcessor"() {
        given:
        String payload = "{\"guid\":\"4ea3be58-3993-46de-88a2-4ffc7f1d73bd\",\"accountId\":0,\"accountType\":\"credit\",\"accountNameOwner\":\"chase_brian\",\"transactionDate\":1553645394,\"description\":\"aliexpress.com\",\"category\":\"online\",\"amount\":3.14,\"cleared\":1,\"reoccurring\":false,\"notes\":\"my note to you\",\"dateUpdated\":1553645394000,\"dateAdded\":1553645394000,\"sha256\":\"963e35c37ea59f3f6fa35d72fb0ba47e1e1523fae867eeeb7ead64b55ff22b77\"}"

        and:
        StringTransactionProcessor processor = new StringTransactionProcessor()

        and:
        Transaction transaction = mapper.readValue(payload, Transaction.class)

        when:
        processor.process(mockExchange)

        then:
        1 * mockExchange.getIn() >> mockMessage
        1 * mockMessage.getBody(Transaction.class) >> transaction
        1 * mockExchange.setProperty('guid', '4ea3be58-3993-46de-88a2-4ffc7f1d73bd')
        //TODO: this should be 1
        0 * mockMessage.setBody(payload)
        1 * _
    }
}
