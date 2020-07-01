//package finance.routes
//
//import finance.configs.CamelProperties
//import finance.processors.ExceptionProcessor
//import finance.processors.JsonTransactionProcessor
//import org.apache.camel.EndpointInject
//import org.apache.camel.Exchange
//import org.apache.camel.Produce
//import org.apache.camel.ProducerTemplate
//import org.apache.camel.builder.RouteBuilder
//import org.apache.camel.component.mock.MockEndpoint
//import org.apache.camel.test.junit4.CamelTestSupport
//import org.junit.Before
//import org.junit.Test
//import org.mockito.InjectMocks
//import org.mockito.MockitoAnnotations
//
//
//import javax.validation.Validator
//
//class JsonFileReaderRouteBuilderTest extends CamelTestSupport {
//
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
//    //@Spy
//    @InjectMocks
//    //@Mock
//    Validator mockValidator
//
//    JsonTransactionProcessor jsonTransactionProcessor
//
//    // @Autowired
//    // JsonTransactionProcessor jsonTransactionProcessor //= new JsonTransactionProcessor(mockValidator)
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
//    String jsonBadInputData = '''
//    [
//    {"guid":"aa08f2bb-29a6-4f71-b866-ff8f625e1b04",
//    "accountNameOwner":"foo_brian",
//    "description":"Bullseye cafe",
//    "category":"restaurant lot and lots and lots and lots and lots and lots and lots and lots and lots and lots",
//    "amount":4.42,
//    "cleared":1,
//    "reoccurring":false,
//    "notes":"","sha256":"","transactionId":0,"accountId":0,
//    "accountType":"creditShouldCauseAParsingError",
//    "transactionDate":1337058000000,
//    "dateUpdated":1487301459000,"dateAdded":1487301459000},
//    {"guid":"bb08f2bb-29a6-4f71-b866-ff8f625e1b04",
//    "accountNameOwner":"foo_brian",
//    "description":"Bullseye cafe","category":"restaurant",
//    "amount":4.42,"cleared":1,
//    "reoccurring":false,"notes":"","sha256":"",
//    "transactionId":0,"accountId":0,
//    "accountType":"credit",
//    "transactionDate":1337058000000,
//    "dateUpdated":1487301459000
//    ,"dateAdded":1487301459000},
//    {"guid":"cc08f2bb-29a6-4f71-b866-ff8f625e1b04",
//    "accountNameOwner":"foo_brian",
//    "description":"Bullseye cafe",
//    "category":"restaurant",
//    "amount":4.42,
//    "cleared":1,
//    "reoccurring":false,
//    "notes":"","sha256":"","transactionId":0,"accountId":0,
//    "accountType":"credit",
//    "transactionDate":1337058000000,
//    "dateUpdated":1487301459000,
//    "dateAdded":1487301459000
//    }
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
//    @Before
//    void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this)
//        jsonTransactionProcessor = new JsonTransactionProcessor(mockValidator)
//    }
//
//
//    //JsonTransactionProcessor
////    JsonTransactionProcessor jsonTransactionProcessor = new JsonTransactionProcessor(mockValidator) {
////        @Override void process(Exchange exchange) {  }
////    }
//
//    ExceptionProcessor exceptionProcessor = new ExceptionProcessor()
//
//    @Override
//    RouteBuilder createRouteBuilder() {
//        new JsonFileReaderRouteBuilder(
//                camelProperties,
//                jsonTransactionProcessor,
//                exceptionProcessor
//        )
//    }
//
//    @Test
//    void testJsonFileReaderRouteBuilder() {
//        String payload = json
//
//        println "payload = $payload"
//        Map<String, Object> headers = new HashMap<>()
//        headers.put(Exchange.FILE_NAME, "file_brian.json")
//
//        println "routeFromLocal = $routeFromLocal"
//
//        routeFromLocal.sendBodyAndHeaders(payload, headers)
//
//        int exchangeCount = toTransactionToDatabaseRoute.receivedExchanges.size()
//
//        println "exchangeCount = $exchangeCount"
//        assertEquals(exchangeCount, 1)
//    }
//
//    @Test
//    void testJsonFileReaderRouteBuilderNotJsonFileName() {
//        String payload = json
//
//        println "payload = $payload"
//        Map<String, Object> headers = new HashMap<>()
//        headers.put(Exchange.FILE_NAME, "foo_brian.notjsonfile")
//        routeFromLocal.sendBodyAndHeaders(payload, headers)
//        int exchangeCount = toFailedJsonFileEndpoint.receivedExchanges.size()
//        assertEquals(1, exchangeCount)
//    }
//
//    @Test
//    void testJsonFileReaderRouteBuilderBadJsonFieldValue() {
//        String payload = jsonBadInputData
//
//        println "payload = $payload"
//        Map<String, Object> headers = new HashMap<>()
//        headers.put(Exchange.FILE_NAME, "foo_brian.json")
//
//        routeFromLocal.sendBodyAndHeaders(payload, headers)
//        int exchangeCount = toTransactionToDatabaseRoute.receivedExchanges.size()
//        println "exchangeCount = $exchangeCount"
//        assertEquals(0, exchangeCount)
//    }
//
//    @Test
//    void testJsonFileReaderRouteBuilderBadJson() {
//        String payload = "foo"
//
//        println "payload = $payload"
//        Map<String, Object> headers = new HashMap<>()
//        headers.put(Exchange.FILE_NAME, "foo_brian.json")
//
//        routeFromLocal.sendBodyAndHeaders(payload, headers)
//        int exchangeCount = toTransactionToDatabaseRoute.receivedExchanges.size()
//        println "exchangeCount = $exchangeCount"
//        assertEquals(0, exchangeCount)
//    }
//}
