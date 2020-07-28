package finance.routes

import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.model.RouteDefinition
import org.apache.camel.reifier.RouteReifier
import spock.lang.Ignore
import spock.lang.Specification

class SimpleRouterSpec extends Specification {

    //def mockSimpleInputService = Mock(SimpleInputService)
    //def mockSimpleOutputService = Mock(SimpleOutputService)
    DefaultCamelContext context

    def setup() {
        def router = new SimpleRouter()
        //  router.simpleInputService = mockSimpleInputService
        //  router.simpleOutputService = mockSimpleOutputService

        DefaultCamelContext context = new DefaultCamelContext()
        context.addRoutes(router)
        context.start()

        ModelCamelContext mcc = context.adapt(ModelCamelContext.class);

        context.routeDefinitions.toList().each { RouteDefinition routeDefinition ->

            println "*** " + routeDefinition
            RouteReifier.adviceWith(mcc.getRouteDefinition("camel-1"), mcc, new AdviceWithRouteBuilder() {
                @Override
                void configure() throws Exception {
                    println "*** test ***"
                    mockEndpointsAndSkip('mock://toTransactionToDatabaseRoute')
                }
            })

        }
    }

    @Ignore
    def 'test -- empty message body with no headers'() {
        given:
        def mockTestOutputEndpoint = MockEndpoint.resolve(context, 'direct:test-output')
        mockTestOutputEndpoint.expectedCount = 0
        def producer = context.createProducerTemplate()
        when:
        producer.sendBody('direct:test-input', '')
        then:
        //1 * mockSimpleInputService.performSimpleStringTask('')
        //0 * mockSimpleOutputService.performSomeOtherSimpleStringTask(_)
        mockTestOutputEndpoint.assertIsSatisfied()
    }
}
