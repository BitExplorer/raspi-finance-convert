package finance.routes

import finance.configs.CamelProperties
import spock.lang.PendingFeature
import spock.lang.Specification

class JsonFileWriterRouteSpec extends Specification {
    def mockRouteUriProperties = Mock(CamelProperties)
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
