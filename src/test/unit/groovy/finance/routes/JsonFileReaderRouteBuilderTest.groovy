package finance.routes

import finance.configs.CamelProperties
import finance.processors.JsonTransactionProcessor
import org.apache.camel.EndpointInject
import org.apache.camel.Exchange
import org.apache.camel.Header
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Ignore
import org.junit.Test

class JsonFileReaderRouteBuilderTest extends CamelTestSupport {

    @Produce(value = 'direct:routeFromLocal')
    ProducerTemplate routeFromLocal

    @EndpointInject(value = 'mock:toSavedFileEndpoint')
    MockEndpoint toSavedFileEndpoint

    CamelProperties camelProperties = new CamelProperties("true", "jsonFileReaderRoute",
            "direct:routeFromLocal",
            "excelFileReaderRoute", "direct:routeFromLocal", "fileWriterRoute",
            "transactionToDatabaseRoute",
            "", "direct:routeFromLocal", "mock:toSavedFileEndpoint", "",
            "")

    JsonTransactionProcessor jsonTransactionProcessor = new JsonTransactionProcessor()

    @Override
    RouteBuilder createRouteBuilder() {
        new JsonFileReaderRouteBuilder (
                camelProperties,
                jsonTransactionProcessor
        )
    }

    @Ignore
    @Test
    void testJsonFileReaderRouteBuilder() {
        String payload = "payload"
        Header header = {
            getProperty() {
                return ""
            }
        } as Header

        routeFromLocal.sendBody(payload)
        int exchangeCount = toSavedFileEndpoint.receivedExchanges.size()
        println "exchangeCount = $exchangeCount"
        assertEquals(exchangeCount, 1)
    }

}
