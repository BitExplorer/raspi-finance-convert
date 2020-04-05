package finance.routes

import finance.configs.AccountProperties
import finance.configs.RouteUriProperties
import finance.processors.ExcelFileProcessor
import finance.processors.JsonTransactionProcessor
import finance.repositories.AccountRepository
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
class JsonFileReaderRoute @Autowired constructor(
        private var jsonTransactionProcessor: JsonTransactionProcessor,
        private var excelFileProcessor: ExcelFileProcessor,
        private var routeUriProperties: RouteUriProperties,
        private var accountProperties: AccountProperties
        //, private var meterRegistry: MeterRegistry
) : RouteBuilder() {

    @Throws(Exception::class)
    override fun configure() {

        // first route
        from("file:${accountProperties.jsonInputFilePath}?delete=true&moveFailed=.failedWithErrors")
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
                  .to("file:${accountProperties.jsonInputFilePath}${File.separator}.notJsonAndNotProcessed")
                  .log(LoggingLevel.INFO, "Not a JSON file, NOT processed successfully.")
                .endChoice()
                .end()

        // first route
        from("file:${accountProperties.excelInputFilePath}?delete=true&moveFailed=.failedWithErrors")
                .autoStartup(routeUriProperties.autoStartRoute)
                .routeId(routeUriProperties.excelFileReaderRouteId)
                .choice()
                .`when`(header("CamelFileName").endsWith(".xlsm"))
                  .setBody(simple("\${file:absolute.path}"))
                  .process(excelFileProcessor)
                  .to("file:${accountProperties.excelInputFilePath}${File.separator}.processed")
                  .log(LoggingLevel.INFO, "Excel file processed successfully.")
                .otherwise()
                  .log(LoggingLevel.INFO, "Not an Excel file, NOT processed successfully.")
                  .to("file:${accountProperties.excelInputFilePath}${File.separator}.notExcelAndNotProcessed")
                .endChoice()
                .end()
    }
}