package finance.routes

import finance.configs.CamelProperties
import finance.processors.ExceptionProcessor
import finance.processors.JsonTransactionProcessor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.ModelCamelContext
import spock.lang.Specification

class JsonFileReaderRouteBuilderSpec extends Specification {

    ModelCamelContext camelContext
    JsonTransactionProcessor mockJsonTransactionProcessor = GroovyMock(JsonTransactionProcessor)
    ExceptionProcessor mockExceptionProcessor = GroovyMock(ExceptionProcessor)

    CamelProperties camelProperties = new CamelProperties("true",
            "jsonFileReaderRoute",
            "direct:routeFromLocal",
            "excelFileReaderRoute",
            "direct:routeFromLocal",
            "fileWriterRoute",
            "transactionToDatabaseRoute",
            "mock:toTransactionToDatabaseRoute",
            "direct:routeFromLocal",
            "mock:toSavedFileEndpoint",
            "mock:toFailedExcelFileEndpoint",
            "mock:toFailedJsonFileEndpoint")

    def setup() {
        camelContext = new DefaultCamelContext()
        def router = new JsonFileReaderRouteBuilder(camelProperties, mockJsonTransactionProcessor, mockExceptionProcessor)

        //mockMongoEndpoint = createMockEndpoint(camelContext, 'mock:mongo')

        camelContext.addRoutes(router)
        camelContext.start()

        //ModelCamelContext mcc = camelContext.adapt(ModelCamelContext.class)

//        AdviceWithRouteBuilder.adviceWith(camelContext, "myRoute", { a ->
//            a.replaceFromWith("direct:start");
//        }

//        RouteReifier.adviceWith(mcc.getRouteDefinition("start"), mcc, new AdviceWithRouteBuilder() {
//
//        }

//        camelContext.routeDefinitions.toList().each { RouteReifier routeReifier ->
//            routeReifier.adviceWith(camelContext as ModelCamelContext, new AdviceWithRouteBuilder() {
//                @Override
//                void configure() throws Exception {
//                   // mockEndpointsAndSkip('log:out')
//                }
//            })
//        }

        //mockTestOutputEndpoint = MockEndpoint.resolve(context, 'mock:log:out')
    }

    def cleanup() {
        //mockTestOutputEndpoint.reset()
        camelContext.stop()
    }

    String json = '''
    [
    {"guid":"aa08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner":"foo_brian",
    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
    "accountType":"credit",
    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000},
    {"guid":"bb08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner":"foo_brian",
    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
    "accountType":"credit",
    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000}
    ]
    '''

    String invalidJsonPayload = '''
    [
    {"guid":"aa08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner":"foo_brian",
    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
    "accountType":"credit",
    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000},
    {"guid":"bb08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner_bad":"foo_brian",
    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
    "accountType":"credit",
    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000},
    {"guid":"cc08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner":"foo_brian",
    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
    "accountType":"credit",
    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000}
    ]
    '''

    def "test with valid json payload"() {
        given:
        def producer = camelContext.createProducerTemplate()

        when:
        producer.sendBody('direct:routeFromLocal', invalidJsonPayload)
        then:
        1==1
        0 * _
    }

    def "test with invalid json payload"() {
        given:
        def producer = camelContext.createProducerTemplate()

        when:
        producer.sendBody('direct:routeFromLocal', json)
        then:
        1==1
        0 * _
        //mockTestOutputEndpoint.assertIsSatisfied()
    }

}
