package finance.processors

import com.fasterxml.jackson.databind.ObjectMapper
import finance.configs.CamelConfig
import finance.models.Transaction
import finance.helpers.TransactionBuilder
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.builder.ExchangeBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultMessage
import spock.lang.Specification

class StringTransactionProcessorSpec extends Specification {
    Exchange exchange = Mock(Exchange)
    Message message = Mock(Message)
    ObjectMapper mapper = new ObjectMapper()

    def "test StringTransactionProcessor" () {

        given:
        String payload = "{\"transactionId\":1002,\"guid\":\"4ea3be58-3993-46de-88a2-4ffc7f1d73bd\",\"accountId\":0,\"accountType\":\"credit\",\"accountNameOwner\":\"chase_brian\",\"transactionDate\":1553645394,\"description\":\"aliexpress.com\",\"category\":\"online\",\"amount\":3.14,\"cleared\":1,\"reoccurring\":false,\"notes\":\"my note to you\",\"dateUpdated\":1553645394000,\"dateAdded\":1553645394000,\"sha256\":\"963e35c37ea59f3f6fa35d72fb0ba47e1e1523fae867eeeb7ead64b55ff22b77\"}"

        and:
        StringTransactionProcessor processor = new StringTransactionProcessor()

        and:
        Transaction transaction = mapper.readValue(payload, Transaction.class)

                when:
        processor.process(exchange)

        then:
        1 * exchange.getIn() >> message
        1 * message.getBody(Transaction.class) >> transaction
        1 * exchange.setProperty('guid', '4ea3be58-3993-46de-88a2-4ffc7f1d73bd')
        1 * message.setBody(payload)
        0 * _
    }
}
