package finance.routes

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import finance.configs.CustomProperties
import finance.configs.CamelProperties
import finance.processors.ExcelFileProcessor
import finance.processors.ExceptionProcessor
import finance.processors.JsonTransactionProcessor
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File
import java.util.LinkedHashMap

@ConditionalOnProperty( name = ["camel.enabled"], havingValue = "true", matchIfMissing = true)
@Component
class JsonFileReaderRouteBuilder @Autowired constructor(
        private var camelProperties: CamelProperties,
        private var jsonTransactionProcessor: JsonTransactionProcessor,
        private var exceptionProcessor: ExceptionProcessor
) : RouteBuilder() {

    @Throws(Exception::class)
    override fun configure() {

        onException(JsonParseException::class.java)
                .log(LoggingLevel.INFO, "Exception trapped :: \${exception.message}")
                .process(exceptionProcessor)
                .handled(true)
                .end()

        onException(UnrecognizedPropertyException::class.java)
                .log(LoggingLevel.INFO, "Exception trapped :: \${exception.message}")
                .process(exceptionProcessor)
                .handled(true)
                .end()

        onException(Exception::class.java)
                .log(LoggingLevel.INFO, "Exception trapped :: \${exception.message}")
                .process(exceptionProcessor)
                .handled(true)
                .end()


        from(camelProperties.jsonFileReaderRoute)
                .autoStartup(camelProperties.autoStartRoute)
                .routeId(camelProperties.jsonFileReaderRouteId)
                .log(camelProperties.jsonFileReaderRouteId)
                .choice()
                .`when`(header("CamelFileName").endsWith(".json"))
                  .log(LoggingLevel.INFO, "new file name: \$simple{file:onlyname.noext}_\$simple{date:now:yyyy-MM-dd}.json")
                  .process(jsonTransactionProcessor)
                  .to(camelProperties.transactionToDatabaseRoute)
                  .log(LoggingLevel.INFO, "JSON file processed successfully.")
                .otherwise()
                  .to(camelProperties.failedJsonFileEndpoint)
                  .log(LoggingLevel.INFO, "Not a JSON file, NOT processed successfully.")
                .endChoice()
                .end()
    }
}