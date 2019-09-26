package finance.routes

import finance.configs.RouteUriProperties
import io.micrometer.core.annotation.Timed
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

@Component
open class FileWriteRoute @Autowired constructor(
        private var routeUriProperties: RouteUriProperties
) : RouteBuilder() {

    @Throws(Exception::class)
    override fun configure() {
        from("direct:jsonFileWriteRoute")
                .autoStartup(routeUriProperties.autoStartRoute)
                //.routeId(routeId)
                .to("file:${routeUriProperties.jsonFilesInputPath}${File.separator}.processed?fileName=\${property.guid}.json&autoCreate=true")
                .end()
    }
}