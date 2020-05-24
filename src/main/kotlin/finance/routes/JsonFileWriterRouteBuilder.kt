package finance.routes

import finance.configs.CustomProperties
import finance.configs.CamelProperties
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
        from(camelProperties.jsonFileWriterRoute)
                .autoStartup(camelProperties.autoStartRoute)
                .routeId(camelProperties.jsonFileWriterRouteId)
                .log(LoggingLevel.INFO, "wrote json file based on guid. \${exchangeProperty.guid}")
                //.to("file:${customProperties.jsonInputFilePath}${File.separator}.processed?fileName=\${exchangeProperty.guid}.json&autoCreate=true")
                .to(camelProperties.savedFileEndpoint)
                .end()
    }
}
