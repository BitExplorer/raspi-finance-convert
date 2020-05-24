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

@Component
class JsonFileReaderRouteBuilder @Autowired constructor(
        private var jsonTransactionProcessor: JsonTransactionProcessor,
        private var excelFileProcessor: ExcelFileProcessor,
        private var camelProperties: CamelProperties,
        private var customProperties: CustomProperties
        //, private var meterRegistry: MeterRegistry
) : RouteBuilder() {

    @Throws(Exception::class)
    override fun configure() {

        // first route
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
                  .to("file:${customProperties.jsonInputFilePath}${File.separator}.notJsonAndNotProcessed")
                  .log(LoggingLevel.INFO, "Not a JSON file, NOT processed successfully.")
                .endChoice()
                .end()

        // first route
        from(camelProperties.excelFileReaderRoute)
                .autoStartup(camelProperties.autoStartRoute)
                .routeId(camelProperties.excelFileReaderRouteId)
                .choice()
                .`when`(header("CamelFileName").endsWith(".xlsm"))
                  .setBody(simple("\${file:absolute.path}"))
                  .process(excelFileProcessor)
                  .to("file:${customProperties.excelInputFilePath}/.processed")
                  .log(LoggingLevel.INFO, "Excel file processed successfully.")
                .otherwise()
                  .log(LoggingLevel.INFO, "Not an Excel file, NOT processed successfully.")
                  .to("file:${customProperties.excelInputFilePath}${File.separator}.notExcelAndNotProcessed")
                .endChoice()
                .end()
    }
}