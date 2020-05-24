package finance.routes

import finance.configs.CamelProperties
import org.apache.camel.EndpointInject
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

class JsonFileWriterRouteBuilderTest extends CamelTestSupport {

    @Produce(value = 'direct:routeFromLocal')
    ProducerTemplate routeFromLocal

    @EndpointInject(value = 'mock:toSavedFileEndpoint')
    MockEndpoint toSavedFileEndpoint

    CamelProperties camelProperties = new CamelProperties("true", "jsonFileReaderRoute", "",
            "excelFileReaderRoute", "", "fileWriterRoute",
            "transactionToDatabaseRoute",
            "", "direct:routeFromLocal", "mock:toSavedFileEndpoint", "",
            "")

    @Override
    RouteBuilder createRouteBuilder() {
        new JsonFileWriterRouteBuilder (
                camelProperties
        )
    }

    @Test
    void testJsonFileWriterRouteBuilder() {
        String payload = "payload"
        routeFromLocal.sendBody(payload)
        int exchangeCount = toSavedFileEndpoint.receivedExchanges.size()
        println "exchangeCount = $exchangeCount"
        assertEquals(exchangeCount, 1)
    }
}
