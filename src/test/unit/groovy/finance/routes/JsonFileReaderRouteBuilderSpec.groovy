package finance.routes

import finance.configs.CamelProperties
import finance.processors.ExceptionProcessor
import finance.processors.JsonTransactionProcessor
import org.apache.camel.Exchange
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.ModelCamelContext
import spock.lang.Specification

import javax.validation.Validator

class JsonFileReaderRouteBuilderSpec extends Specification {

    ModelCamelContext camelContext
    Validator mockValidator = GroovyMock(Validator)
    //JsonTransactionProcessor mockJsonTransactionProcessor = GroovyMock(JsonTransactionProcessor)
    JsonTransactionProcessor jsonTransactionProcessor = new JsonTransactionProcessor(mockValidator)
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
        println "setup started."
        camelContext = new DefaultCamelContext()
        def router = new JsonFileReaderRouteBuilder(camelProperties, jsonTransactionProcessor, mockExceptionProcessor)
        camelContext.addRoutes(router)
        camelContext.start()

        ModelCamelContext mcc = camelContext.adapt(ModelCamelContext.class)

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

    String payload = '''
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
    "description":"Bullseye cafe","category":"restaurant",
    "amount":4.42,"cleared":1,
    "reoccurring":false,"notes":"",
    "sha256":"","transactionId":0,
    "accountId":0,
    "accountType":"credit",
    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000},
    {"guid":"bb08f2bb-29a6-4f71-b866-ff8f625e1b04",
    "accountNameOwner":"foo_brian",
    "description":"Bullseye cafe",
    "category":"restaurant","amount":4.42,"cleared":1,
    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
    "accountType":"creditNotValid",
    "transactionDate":1337058000000,
    "dateUpdated":1487301459000,
    "dateAdded":1487301459000},
    {"guid":"cc08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner":"foo_brian",
    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
    "accountType":"credit",
    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000}
    ]
    '''

    //TODO: needs work
    def "test with valid json payload"() {
        given:
        def producer = camelContext.createProducerTemplate()
        producer.setDefaultEndpointUri('direct:routeFromLocal')
        Map<String, Object> headers = new HashMap<>()
        headers.put(Exchange.FILE_NAME, "foo_brian.json")

        when:
        //producer.sendBody('direct:routeFromLocal', invalidJsonPayload)
        //producer.sendBodyAndHeaders(payload, headers)
        producer.sendBodyAndHeader('direct:routeFromLocal', payload, headers)
        def ctx = producer.camelContext
        println "endpoints - " + ctx.getEndpoints()

//        println "payload = $payload"
//        Map<String, Object> headers = new HashMap<>()
//        headers.put(Exchange.FILE_NAME, "foo_brian.notjsonfile")
//        routeFromLocal.sendBodyAndHeaders(payload, headers)
        //int exchangeCount = toFailedJsonFileEndpoint.receivedExchanges.size()
//        assertEquals(1, exchangeCount)


        then:
        //1 * mockValidator.validate(_)
        0 * _
    }

    //TODO: needs work
    def "test with invalid json payload"() {
        given:
        def producer = camelContext.createProducerTemplate()
        producer.setDefaultEndpointUri('direct:routeFromLocal')
        Map<String, Object> headers = new HashMap<>()
        headers.put(Exchange.FILE_NAME, "foo_brian.json")

        when:
        producer.sendBody('direct:routeFromLocal', invalidJsonPayload)
        then:
        0 * _
    }

    // TODO: this is not working
    def 'test -- message with headers'() {
        given:
        def mockTestOutputEndpoint = MockEndpoint.resolve(camelContext, 'mock://toTransactionToDatabaseRoute')
        mockTestOutputEndpoint.expectedCount = 0
        def producer = camelContext.createProducerTemplate()
        producer.setDefaultEndpointUri('direct:routeFromLocal')
        Map<String, Object> headers = new HashMap<>()
        headers.put(Exchange.FILE_NAME, "foo_brian.json")

        when:
        //producer.sendBody('direct:routeFromLocal', 'foo')
        producer.sendBody('direct:routeFromLocal', payload)
        then:
        //1 * jsonTransactionProcessor.validator.validate(_)
        //1 * mockSimpleInputService.performSimpleStringTask('')
        //0 * mockSimpleOutputService.performSomeOtherSimpleStringTask(_)
        mockTestOutputEndpoint.assertIsSatisfied()
        0 * _
    }

}
