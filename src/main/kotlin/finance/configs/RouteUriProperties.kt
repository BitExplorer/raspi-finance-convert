package finance.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.validation.constraints.NotNull

@Component
@ConfigurationProperties(prefix = "route", ignoreUnknownFields = false)
class RouteUriProperties {

    @NotNull
    lateinit var activemqFinanceDropProducer: String

    @NotNull
    lateinit var directToActivemqUri: String

    @NotNull
    lateinit var sedaOutputUri: String

    @NotNull
    lateinit var filePollerUri: String

    @NotNull
    lateinit var autoStartRoute: String

    @NotNull
    lateinit var jsonFilesInputPath: String

    @NotNull
    lateinit var activemqFinanceDropConsumer : String

    @NotNull
    lateinit var databaseInsertRoute: String

    @NotNull
    lateinit var directToActivemqRoute: String

    @NotNull
    lateinit var jsonFileReaderRoute: String

    @NotNull
    lateinit var sedaQueueToDirectActivemqRoute: String

    @NotNull
    lateinit var transactionToSedaQueueRoute: String

    @NotNull
    lateinit var fileWriteRoute: String
}
