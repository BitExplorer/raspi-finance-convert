package finance.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "custom.project.camel-route", ignoreUnknownFields = false)
open class CamelProperties(
    var autoStartRoute: String = "",
    var jsonFileReaderRouteId: String = "",
    var excelFileReaderRouteId: String = "",
    var jsonFileWriterRouteId: String = "",
    var transactionToDatabaseRouteId: String = "",
    var processEachTransaction: String = "",
    var jsonFileWriterRoute: String = "",
    var savedFileEndpoint: String = ""
)
