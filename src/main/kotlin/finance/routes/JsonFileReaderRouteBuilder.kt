package finance.routes

import finance.configs.CustomProperties
import finance.configs.CamelProperties
import finance.processors.ExcelFileProcessor
import finance.processors.JsonTransactionProcessor
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.util.LinkedHashMap

@Component
open class JsonFileReaderRouteBuilder @Autowired constructor(
        private var camelProperties: CamelProperties,
        private var jsonTransactionProcessor: JsonTransactionProcessor
) : RouteBuilder() {

    @Throws(Exception::class)
    override fun configure() {

        from(camelProperties.jsonFileReaderRoute)
                .autoStartup(camelProperties.autoStartRoute)
                .routeId(camelProperties.jsonFileReaderRouteId)
                .log(camelProperties.jsonFileReaderRouteId)
                .choice()
                .`when`(header("CamelFileName").endsWith(".json"))
                  .log(LoggingLevel.INFO, "\$simple{file:onlyname.noext}_\$simple{date:now:yyyy-MM-dd}.json")
                  .process(jsonTransactionProcessor)
                  .to(camelProperties.transactionToDatabaseRoute)
                  .log(LoggingLevel.INFO, "JSON file processed successfully.")
                .otherwise()
                  .to(camelProperties.failedJsonFileEndpoint)
                  .log(LoggingLevel.WARN, "Not a JSON file, NOT processed successfully.")
                .endChoice()
                .end()
    }
}