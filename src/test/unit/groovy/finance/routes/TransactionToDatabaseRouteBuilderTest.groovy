//package finance.routes
//
//import finance.configs.CamelProperties
//import finance.processors.ExceptionProcessor
//import finance.processors.InsertTransactionProcessor
//import finance.processors.JsonTransactionProcessor
//import finance.processors.StringTransactionProcessor
//import finance.services.TransactionService
//import org.apache.camel.EndpointInject
//import org.apache.camel.Exchange
//import org.apache.camel.Produce
//import org.apache.camel.ProducerTemplate
//import org.apache.camel.builder.RouteBuilder
//import org.apache.camel.component.mock.MockEndpoint
//import org.apache.camel.test.junit4.CamelTestSupport
//import org.junit.Ignore
//import org.junit.Test
//import org.mockito.Mock
//import spock.lang.PendingFeature
//import spock.lang.Specification
//
//class TransactionToDatabaseRouteBuilderTest extends CamelTestSupport {
//    @Produce(value = 'direct:routeFromLocal')
//    ProducerTemplate routeFromLocal
//
//    @EndpointInject(value = 'mock:toSavedFileEndpoint')
//    MockEndpoint toSavedFileEndpoint
//
//    @EndpointInject(value = 'mock:toFailedJsonFileEndpoint')
//    MockEndpoint toFailedJsonFileEndpoint
//
//    @EndpointInject(value = 'mock:toTransactionToDatabaseRoute')
//    MockEndpoint toTransactionToDatabaseRoute
//
//    @Mock
//    TransactionService mockTransactionService
//
//
//    String json = '''
//    [
//    {"guid":"aa08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner":"foo_brian",
//    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
//    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
//    "accountType":"credit",
//    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000},
//    {"guid":"bb08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner":"foo_brian",
//    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
//    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
//    "accountType":"credit",
//    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000}
//    ]
//    '''
//
//    String jsonBad = '''
//    [
//    {"guid":"aa08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner":"foo_brian",
//    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
//    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
//    "accountType":"credit",
//    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000},
//    {"guid":"bb08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner_bad":"foo_brian",
//    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
//    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
//    "accountType":"credit",
//    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000},
//    {"guid":"cc08f2bb-29a6-4f71-b866-ff8f625e1b04","accountNameOwner":"foo_brian",
//    "description":"Bullseye cafe","category":"restaurant","amount":4.42,"cleared":1,
//    "reoccurring":false,"notes":"","sha256":"","transactionId":0,"accountId":0,
//    "accountType":"credit",
//    "transactionDate":1337058000000,"dateUpdated":1487301459000,"dateAdded":1487301459000}
//    ]
//    '''
//
//    CamelProperties camelProperties = new CamelProperties("true",
//            "jsonFileReaderRoute",
//            "direct:routeFromLocal",
//            "excelFileReaderRoute",
//            "direct:routeFromLocal",
//            "fileWriterRoute",
//            "transactionToDatabaseRoute",
//            "mock:toTransactionToDatabaseRoute",
//            "direct:routeFromLocal",
//            "mock:toSavedFileEndpoint",
//            "mock:toFailedExcelFileEndpoint",
//            "mock:toFailedJsonFileEndpoint")
//
//
//    StringTransactionProcessor stringTransactionProcessor = new StringTransactionProcessor()
//    InsertTransactionProcessor insertTransactionProcessor = new InsertTransactionProcessor(mockTransactionService)
//    ExceptionProcessor exceptionProcessor = new ExceptionProcessor()
//
//    @Override
//    RouteBuilder createRouteBuilder() {
//        new TransactionToDatabaseRouteBuilder (
//                camelProperties,
//                stringTransactionProcessor,
//                insertTransactionProcessor,
//                exceptionProcessor
//        )
//    }
//
//
//    @Ignore
//    @Test
//    void testJsonFileReaderRouteBuilder() {
//        String payload = json
//
//        println "payload = $payload"
//        Map<String, Object> headers = new HashMap<>()
//        headers.put(Exchange.FILE_NAME, "file_brian.json")
//        routeFromLocal.sendBodyAndHeaders(payload, headers)
//        int exchangeCount = toTransactionToDatabaseRoute.receivedExchanges.size()
//        println "exchangeCount = $exchangeCount"
//        assertEquals(exchangeCount, 1)
//    }
//}
