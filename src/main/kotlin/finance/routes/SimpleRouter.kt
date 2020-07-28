package finance.routes


import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class SimpleRouter  : RouteBuilder() {

//    @Autowired
//    SimpleInputService simpleInputService
//
//            @Autowired
//            SimpleOutputService simpleOutputService

    @Throws(Exception::class)
    override fun configure() {

        from("direct:test-input")
                .log("Received message on test-input")
                //.bean(simpleInputService)
                .choice()
                .`when`(header("SEND_OUT").isNotNull)
                .log("Message is valid and will be sent to direct:test-output")
                .to("direct:test-output")
                .endChoice()

        from("direct:test-output")
                .log("Received message on test-output")
                //.bean(simpleOutputService)
                .to("log:out")
    }
}