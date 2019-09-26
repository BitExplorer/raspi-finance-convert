package finance.routes

import finance.configs.RouteUriProperties
import finance.models.Transaction
import finance.processors.InsertTransactionProcessor
import finance.processors.StringTransactionProcessor
import io.micrometer.core.annotation.Timed
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File

@Component
open class TransactionToDatabaseRoute @Autowired constructor(
        private var stringTransactionProcessor: StringTransactionProcessor,
        private var insertTransactionProcessor: InsertTransactionProcessor,
        private var routeUriProperties: RouteUriProperties
) : RouteBuilder() {

    @Throws(Exception::class)
    override fun configure() {
        from("direct:processEachTransaction")
                .autoStartup(routeUriProperties.autoStartRoute)
                .routeId("transactionToDatabase")
                .split(body())
                .log(LoggingLevel.INFO, "split body")
                .convertBodyTo(Transaction::class.java)
                .log(LoggingLevel.INFO, "process string")
                .process(stringTransactionProcessor)
                .convertBodyTo(String::class.java)
                .to("file:${routeUriProperties.jsonFilesInputPath}${File.separator}.processed?fileName=\${property.guid}.json&autoCreate=true")
                //.to(routeUriProperties.sedaOutputUri)
                //.to(routeUriProperties.databaseInsertRoute)

                //.convertBodyTo(String::class.java)
                .process(insertTransactionProcessor)
                .log(LoggingLevel.INFO, "*** message was processed by insertTransactionProcessor. ***")

                .end()
    }
}