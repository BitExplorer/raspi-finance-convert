package finance.routes

import finance.configs.RouteUriProperties
import spock.lang.PendingFeature
import spock.lang.Specification

class JsonFileWriterRouteSpec extends Specification {
    def mockRouteUriProperties = Mock(RouteUriProperties)
    def route = new JsonFileWriterRoute(mockRouteUriProperties)
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
