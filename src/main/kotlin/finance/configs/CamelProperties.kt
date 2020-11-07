package finance.configs

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(name = ["camel.enabled"], havingValue = "true", matchIfMissing = true)
@Configuration
@ConfigurationProperties(prefix = "custom.project.camel-route", ignoreUnknownFields = true)
open class CamelProperties(
        var autoStartRoute: String = "",
        var jsonFileReaderRouteId: String = "",
        var jsonFileReaderRoute: String = "",
        var excelFileReaderRouteId: String = "",
        var excelFileReaderRoute: String = "",
        var jsonFileWriterRouteId: String = "",
        var transactionToDatabaseRouteId: String = "",
        var transactionToDatabaseRoute: String = "",
        var jsonFileWriterRoute: String = "",
        var savedFileEndpoint: String = "",
        var failedExcelFileEndpoint: String = "",
        var failedJsonFileEndpoint: String = "",
        var failedJsonParserEndpoint: String = ""
//        val jsonParseException: Class<JsonParseException> = JsonParseException::class.java,
//        val unrecognizedPropertyException: Class<UnrecognizedPropertyException> = UnrecognizedPropertyException::class.java,
//        val exception : Class<Exception> = Exception::class.java
) {
    constructor() : this(savedFileEndpoint = "")
}
