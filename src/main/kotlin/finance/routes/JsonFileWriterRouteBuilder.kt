package finance.routes

import finance.configs.CustomProperties
import finance.configs.CamelProperties
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.support.component.PropertyConfigurerSupport.property
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File

@ConditionalOnProperty(name = ["camel.enabled"], havingValue = "true", matchIfMissing = true)
@Component
class JsonFileWriterRouteBuilder @Autowired constructor(
        private var camelProperties: CamelProperties
) : RouteBuilder() {

    //val x = UUID.randomUUID()

    @Throws(Exception::class)
    override fun configure() {
        println(camelProperties.savedFileEndpoint)

        onException(Exception::class.java)
                .log(LoggingLevel.INFO, "Exception trapped :: \${exception.message}")
                .handled(true)
                .end()

        from(camelProperties.jsonFileWriterRoute)
                .autoStartup(camelProperties.autoStartRoute)
                .routeId(camelProperties.jsonFileWriterRouteId)
                //TODO: bh fix this to address the unique file name
                .setHeader(Exchange.FILE_NAME, header("guid"))
                //.setHeader(Exchange.FILE_NAME, "\${exchangeProperty.guid}")
                .log(LoggingLevel.INFO, "wrote processed data to file.")
                //message.setHeader(Exchange.FILE_NAME, filename);
                //.to("file:${customProperties.jsonInputFilePath}${File.separator}.processed?fileName=\${exchangeProperty.guid}.json&autoCreate=true")
                .to(camelProperties.savedFileEndpoint)
                .end()
    }
}
