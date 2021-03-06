package finance.routes

import finance.configs.CamelProperties
import finance.processors.ExcelFileProcessor
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@ConditionalOnProperty(name = ["camel.enabled"], havingValue = "true", matchIfMissing = true)
@Component
class ExcelFileReaderRouteBuilder @Autowired constructor(
        private var camelProperties: CamelProperties,
        private var excelFileProcessor: ExcelFileProcessor
) : RouteBuilder() {

    @Throws(Exception::class)
    override fun configure() {

        from(camelProperties.excelFileReaderRoute)
                .autoStartup(camelProperties.autoStartRoute)
                .routeId(camelProperties.excelFileReaderRouteId)
                .choice()
                .`when`(header("CamelFileName").endsWith(".xlsm"))
                .setBody(simple("\${file:absolute.path}"))
                .process(excelFileProcessor)
                .log(LoggingLevel.INFO, "Excel file processed successfully.")
                .otherwise()
                .log(LoggingLevel.WARN, "Not an Excel file, NOT processed successfully.")
                .to(camelProperties.failedExcelFileEndpoint)
                .endChoice()
                .end()
    }
}