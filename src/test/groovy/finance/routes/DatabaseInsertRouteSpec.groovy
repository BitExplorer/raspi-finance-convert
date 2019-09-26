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

//https://stackoverflow.com/questions/23434232/how-to-unit-test-this-route-builder-in-camel
//@PendingFeature

class DatabaseInsertRouteSpec extends Specification {

    def setup() {
    }

    def cleanup() {

    }

    @PendingFeature
    def "test DatabaseInsertRoute"() {
        1== 2
    }
}
