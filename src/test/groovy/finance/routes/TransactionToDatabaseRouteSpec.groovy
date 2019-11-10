package finance.routes

import finance.configs.RouteUriProperties
import finance.processors.InsertTransactionProcessor
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.model.RouteDefinition
import spock.lang.PendingFeature
import spock.lang.Specification

class TransactionToDatabaseRouteSpec extends Specification {
    def mockRouteUriProperties = Mock(RouteUriProperties)
    def mockInsertTransactionProcessor = Mock(InsertTransactionProcessor)
    def mockStringTransactionProcessor = Mock(StringTransactionProcessor)


    def setup() {
        def route = new TransactionToDatabaseRoute()
        route.insertTransactionProcessor = mockInsertTransactionProcessor
        route.stringTransactionProcessor = mockStringTransactionProcessor
    }

    def cleanup() {

    }

    @PendingFeature
    def "test TransactionToDatabaseRoute"() {
        1== 2
    }
}
