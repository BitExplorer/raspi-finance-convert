package finance.routes

import finance.processors.JsonTransactionProcessor
import spock.lang.PendingFeature
import spock.lang.Specification

class JsonFileReaderRouteBuilderSpec extends Specification {
    def mockJsonTransactionProcessor = Mock(JsonTransactionProcessor)

    def setup() {
        def route = new JsonFileReaderRouteBuilder()
        route.jsonTransactionProcessor = mockJsonTransactionProcessor
    }

    @PendingFeature
    def "test JsonFileReaderRoute"() {
        1==2
    }
}
