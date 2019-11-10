package finance.routes

import finance.processors.JsonTransactionProcessor
import spock.lang.PendingFeature
import spock.lang.Specification

class JsonFileReaderRouteSpec extends Specification {
    def mockJsonTransactionProcessor = Mock(JsonTransactionProcessor)

    def setup() {
        def route = new JsonFileReaderRoute()
        route.jsonTransactionProcessor = mockJsonTransactionProcessor
    }

    @PendingFeature
    def "test JsonFileReaderRoute"() {
        1==2
    }
}
