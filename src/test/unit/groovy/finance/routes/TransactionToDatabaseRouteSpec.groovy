package finance.routes

import finance.configs.CamelProperties
import finance.processors.InsertTransactionProcessor
import spock.lang.PendingFeature
import spock.lang.Specification

class TransactionToDatabaseRouteSpec extends Specification {
    def mockRouteUriProperties = Mock(CamelProperties)
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
