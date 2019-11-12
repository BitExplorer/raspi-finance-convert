package finance.routes

import finance.configs.RouteUriProperties
import finance.models.Transaction
import finance.processors.InsertTransactionProcessor
import finance.processors.StringTransactionProcessor
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class TransactionToDatabaseRoute @Autowired constructor(
        private var stringTransactionProcessor: StringTransactionProcessor,
        private var insertTransactionProcessor: InsertTransactionProcessor,
        private var routeUriProperties: RouteUriProperties
) : RouteBuilder() {

    @Throws(Exception::class)
    override fun configure() {
        from(routeUriProperties.processEachTransaction)
                .autoStartup(routeUriProperties.autoStartRoute)
                .routeId(routeUriProperties.transactionToDatabaseRouteId)
                .split(body())
                .log(LoggingLevel.INFO, "split body completed.")
                .convertBodyTo(Transaction::class.java)
                .log(LoggingLevel.INFO, "converted body to string.")
                .process(stringTransactionProcessor)
                .convertBodyTo(String::class.java)
                .to(routeUriProperties.jsonFileWriterRoute)
                .process(insertTransactionProcessor)
                .log(LoggingLevel.INFO, "message was processed by insertTransactionProcessor.")
                .end()
    }
}