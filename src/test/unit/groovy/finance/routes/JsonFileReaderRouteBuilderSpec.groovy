package finance.routes

import finance.configs.CamelProperties
import finance.processors.ExceptionProcessor
import finance.processors.JsonTransactionProcessor
import org.apache.camel.EndpointInject
import org.apache.camel.Exchange
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.model.RouteDefinition
import org.apache.camel.reifier.RouteReifier
import spock.lang.Ignore
import spock.lang.Specification

import javax.validation.Validator

class JsonFileReaderRouteBuilderSpec extends Specification {

    ModelCamelContext camelContext
    Validator mockValidator = GroovyMock(Validator)
    JsonTransactionProcessor mockJsonTransactionProcessor = GroovyMock(JsonTransactionProcessor)
    //JsonTransactionProcessor jsonTransactionProcessor = new JsonTransactionProcessor(mockValidator)
    ExceptionProcessor mockExceptionProcessor = GroovyMock(ExceptionProcessor)

//    MockEndpoint mockToTransactionToDatabaseRouteEndpoint
//    MockEndpoint toFailedJsonFileEndpoint
//
////    @EndpointInject(value = 'mock://toTransactionToDatabaseRoute')
////    MockEndpoint toTransactionToDatabaseRoute

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
        router.jsonTransactionProcessor = mockJsonTransactionProcessor
        camelContext.addRoutes(router)

        camelContext.start()

        ModelCamelContext mcc = camelContext.adapt(ModelCamelContext.class)

        camelContext.routeDefinitions.toList().each { RouteDefinition routeDefinition ->

            println "*** " + routeDefinition
            RouteReifier.adviceWith(mcc.getRouteDefinition(camelProperties.jsonFileReaderRouteId), mcc, new AdviceWithRouteBuilder() {
                @Override
                void configure() throws Exception {
                    println "*** test ***"
                    mockEndpointsAndSkip('direct://toTransactionToDatabaseRoute')
                    //mockEndpoints('direct://toTransactionToDatabaseRoute')
                }
            })

//            routeDefinition.adviceWith(camelContext as ModelCamelContext, new AdviceWithRouteBuilder() {
//                @Override
//                void configure() throws Exception {
//                    mockEndpointsAndSkip('direct:routeFromLocal')
//                }
//            })
        }

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

    @Ignore
    def 'test -- empty message body && valid output header'(){
        given:
        def mockTestOutputEndpoint = MockEndpoint.resolve(camelContext, 'mock://toTransactionToDatabaseRoute')
        mockTestOutputEndpoint.expectedCount = 1
        def producer = camelContext.createProducerTemplate()
        Map<String, Object> headers = new HashMap<>()
        headers.put(Exchange.FILE_NAME, "foo_brian.json")
        producer.setDefaultEndpointUri('direct:routeFromLocal')

        when:
        producer.sendBodyAndHeader('direct:routeFromLocal', payload, headers)

        then:
        //1 * mockSimpleInputService.performSimpleStringTask('')
        //1 * mockSimpleOutputService.performSomeOtherSimpleStringTask(_)
        mockTestOutputEndpoint.assertIsSatisfied()
        0 * _
    }

    //TODO: needs work
    def "test with valid json payload - updated"() {
        given:
        def producer = camelContext.createProducerTemplate()
        producer.setDefaultEndpointUri('direct:routeFromLocal')
        Map<String, Object> headers = new HashMap<>()
        headers.put(Exchange.FILE_NAME, "foo_brian.json")

        when:
        producer.sendBodyAndHeader('direct:routeFromLocal', payload, headers)
        //def context = producer.camelContext
        MockEndpoint mockToTransactionToDatabaseRouteEndpoint = MockEndpoint.resolve(camelContext, 'mock://toTransactionToDatabaseRoute')
        MockEndpoint toFailedJsonFileEndpoint = MockEndpoint.resolve(camelContext, 'mock:toFailedJsonFileEndpoint')
        println "****" + mockToTransactionToDatabaseRouteEndpoint
        mockToTransactionToDatabaseRouteEndpoint.expectedCount = 0
        toFailedJsonFileEndpoint.expectedCount = 0

        then:
        mockToTransactionToDatabaseRouteEndpoint.assertIsSatisfied()
        0 * _
    }

    def 'test with invalid file name'() {
        given:
        def mockTestOutputEndpoint = MockEndpoint.resolve(camelContext, camelProperties.failedJsonFileEndpoint)
        mockTestOutputEndpoint.expectedCount = 1
        def producer = camelContext.createProducerTemplate()
        producer.setDefaultEndpointUri('direct:routeFromLocal')

        when:
        producer.sendBodyAndHeader(payload, Exchange.FILE_NAME, 'foo_brian.bad')

        then:
        mockTestOutputEndpoint.receivedExchanges.size() == 1
        mockTestOutputEndpoint.assertIsSatisfied()
        0 * _
    }

    def 'test -- valid payload and valid fileName'() {
        given:
        def mockTestOutputEndpoint = MockEndpoint.resolve(camelContext, camelProperties.transactionToDatabaseRoute)
        mockTestOutputEndpoint.expectedCount = 1
        def producer = camelContext.createProducerTemplate()
        producer.setDefaultEndpointUri('direct:routeFromLocal')

        when:
        producer.sendBodyAndHeader(payload, Exchange.FILE_NAME, 'foo_brian.json')

        then:
        mockTestOutputEndpoint.receivedExchanges.size() == 1
        mockTestOutputEndpoint.assertIsSatisfied()
        0 * _
    }

    def 'test -- invalid payload and valid fileName'() {
        given:
        def mockTestOutputEndpoint = MockEndpoint.resolve(camelContext, camelProperties.transactionToDatabaseRoute)
        mockTestOutputEndpoint.expectedCount = 0
        def producer = camelContext.createProducerTemplate()
        producer.setDefaultEndpointUri('direct:routeFromLocal')

        when:
        producer.sendBodyAndHeader(invalidJsonPayload, Exchange.FILE_NAME, 'foo_brian.json')

        then:
        mockTestOutputEndpoint.receivedExchanges.size() == 0
        mockTestOutputEndpoint.assertIsSatisfied()
        0 * _
    }
}
