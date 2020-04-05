package finance.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "custom.project")
@Configuration
open class AccountProperties(
        var creditAccounts: MutableList<String> = mutableListOf(),
        var excludedAccounts: MutableList<String> = mutableListOf(),
        var timeZone: String = "",
        var excelPassword: String = "",
        var jsonInputFilePath: String = "",
        var excelInputFilePath: String = ""
)