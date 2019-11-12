package finance.routes

import finance.configs.RouteUriProperties
import finance.processors.JsonTransactionProcessor
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
class JsonFileReaderRoute @Autowired constructor(
        private var jsonTransactionProcessor: JsonTransactionProcessor,
        private var routeUriProperties: RouteUriProperties
        //, private var meterRegistry: MeterRegistry
) : RouteBuilder() {

    @Throws(Exception::class)
    override fun configure() {

        // first route
        from("file:${routeUriProperties.jsonFilesInputPath}?delete=true&moveFailed=.failedWithErrors")
                .autoStartup(routeUriProperties.autoStartRoute)
                .routeId(routeUriProperties.jsonFileReaderRouteId)
                .log(routeUriProperties.jsonFileReaderRouteId)
                .choice()
                .`when`(header("CamelFileName").endsWith(".json"))
                  .log(LoggingLevel.INFO, "\$simple{file:onlyname.noext}_\$simple{date:now:yyyy-MM-dd}.json")
                  .process(jsonTransactionProcessor)
                  .to(routeUriProperties.processEachTransaction)
                  .log(LoggingLevel.INFO, "JSON file processed successfully.")
                .otherwise()
                  .to("file:${routeUriProperties.jsonFilesInputPath}${File.separator}.notJsonAndnotProcessed")
                  .log(LoggingLevel.INFO, "Not a JSON file, NOT processed successfully.")
                .endChoice()
                .end()
    }
}