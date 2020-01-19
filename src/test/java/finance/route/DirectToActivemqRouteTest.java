//package finance.route;
//
//import org.apache.camel.EndpointInject;
//import org.apache.camel.ProducerTemplate;
//import org.apache.camel.component.mock.MockEndpoint;
//import org.apache.camel.test.spring.CamelSpringBootRunner;
//import org.apache.camel.test.spring.MockEndpoints;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
////@RunWith(CamelSpringBootRunner.class)
//@MockEndpoints("activemq:queue:finance_drop")
//public class DirectToActivemqRouteTest {
//
////    @Autowired
////    private ProducerTemplate producerTemplate;
//
////    @Autowired
////    public DirectToActivemqRouteTest(ProducerTemplate producerTemplate) {
////       this.producerTemplate = producerTemplate;
////    }
//
//    @EndpointInject(uri = "mock:activemq:queue:finance_drop")
//    private MockEndpoint mockEndpoint;
//
////    @Test
////    public void testActivemqRouteTest1() throws InterruptedException {
////        mockEndpoint.expectedBodiesReceived("blah");
////        //producerTemplate.sendBody("direct:sendToActivemq", "blah");
////        mockEndpoint.assertIsSatisfied();
////    }
//
//    @Test
//    public void testActivemqRouteTest() {
//        assert(true);
//    }
//}
