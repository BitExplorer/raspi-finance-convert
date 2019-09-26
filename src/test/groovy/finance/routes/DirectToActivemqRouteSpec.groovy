package finance.routes

import finance.configs.RouteUriProperties
import org.apache.camel.EndpointInject
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.model.RouteDefinition
import spock.lang.PendingFeature
import spock.lang.Specification

//@Context(routeBuilders = [DirectToActivemqRoute])
//@Mixin(CamelSpockUtils)
//@MockEndpoints("activemq:queue:finance_drop")
class DirectToActivemqRouteSpec extends Specification {
    RouteUriProperties routeUriProperties = new RouteUriProperties()
    DirectToActivemqRoute activemqRoute = new DirectToActivemqRoute(routeUriProperties)

    @EndpointInject(uri = "mock:activemq:queue:finance_drop")
    private MockEndpoint mockEndpoint

    //@Endpoint("activemq:queue:finance_drop")
    //CamelMock camelMock = Mock()

    //@Produce
//https://github.com/TouK/camel-spock/blob/e524a53486b847fabbfb9f57af913b148e2540ab/src/test/groovy/pl/touk/camelSpock/MockSendEndpointsSpec.groovy

    @PendingFeature
    def "Test DirectToActivemqRoute"() {
        given:
        def camelContext = new DefaultCamelContext()

        when:
        camelContext.addRoutes(activemqRoute)
        camelContext.setAutoStartup(true)
        camelContext.start()

        camelContext.routeDefinitions.toList().each { RouteDefinition routeDefinition ->
            routeDefinition.adviceWith(camelContext as ModelCamelContext, new AdviceWithRouteBuilder() {
                @Override
                void configure() throws Exception {
                    mockEndpointsAndSkip("activemq:queue:finance_drop")
                }
            })
        }

        def producer = camelContext.createProducerTemplate()
        producer.sendBody("direct:sendToActivemq", "blah")

        then:
        camelContext.isStarted()
        1 == 1
    }
}