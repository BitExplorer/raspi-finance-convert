package finance.routes

import finance.configs.CamelProperties
import org.apache.camel.test.junit4.CamelTestSupport
import spock.lang.PendingFeature
import spock.lang.Specification

class JsonFileWriterRouteBuilderSpec extends CamelTestSupport {
    def mockRouteUriProperties = Mock(CamelProperties)
    def route = new JsonFileWriterRouteBuilder(mockRouteUriProperties)
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
