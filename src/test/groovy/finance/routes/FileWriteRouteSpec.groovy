package finance.routes

import finance.configs.RouteUriProperties
import org.apache.camel.component.mock.MockEndpoint
import spock.lang.PendingFeature
import spock.lang.Specification

class FileWriteRouteSpec extends Specification {
    def mockRouteUriProperties = Mock(RouteUriProperties)
    def route = new FileWriteRoute(mockRouteUriProperties)
    //def MockEndpoint mockEndpoint;

    def setup() {
        //autoStartRoute
    }

    @PendingFeature
    def "test FileWriteRoute"() {
        //given:
        when:
          route.configure()
        then:
          _ * 1== 1
    }
}
