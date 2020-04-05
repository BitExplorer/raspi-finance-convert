package finance.routes

import finance.configs.AccountProperties
import finance.configs.RouteUriProperties
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
open class JsonFileWriterRoute @Autowired constructor(
        private var routeUriProperties: RouteUriProperties,
        private var accountProperties: AccountProperties
) : RouteBuilder() {

    //val x = UUID.randomUUID()

    @Throws(Exception::class)
    override fun configure() {
        from(routeUriProperties.jsonFileWriterRoute)
                .autoStartup(routeUriProperties.autoStartRoute)
                .routeId(routeUriProperties.jsonFileWriterRouteId)
                .log(LoggingLevel.INFO, "wrote json file based on guid. \${exchangeProperty.guid}")
                .to("file:${accountProperties.jsonInputFilePath}${File.separator}.processed?fileName=\${exchangeProperty.guid}.json&autoCreate=true")
                .end()
    }
}
