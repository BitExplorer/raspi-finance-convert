package finance.routes

import finance.configs.CustomProperties
import finance.configs.CamelProperties
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
open class JsonFileWriterRouteBuilder @Autowired constructor(
        private var camelProperties: CamelProperties,
        private var customProperties: CustomProperties
) : RouteBuilder() {

    //val x = UUID.randomUUID()

    @Throws(Exception::class)
    override fun configure() {
        println(camelProperties.savedFileEndpoint)

        onException(Exception::class.java)
                .log(LoggingLevel.INFO, "major failure in the FileWriterRoute.")
                .handled(true)
                .end()

        from(camelProperties.jsonFileWriterRoute)
                .autoStartup(camelProperties.autoStartRoute)
                .routeId(camelProperties.jsonFileWriterRouteId)
                //.setHeader(Exchange.FILE_NAME, "\${exchangeProperty.guid}")
                .log(LoggingLevel.INFO, "wrote processed data to file.")
        //message.setHeader(Exchange.FILE_NAME, filename);
        //.to("file:${customProperties.jsonInputFilePath}${File.separator}.processed?fileName=\${exchangeProperty.guid}.json&autoCreate=true")
                .to(camelProperties.savedFileEndpoint)
                .end()
    }
}
