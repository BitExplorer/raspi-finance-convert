package finance.routes

import finance.configs.CamelProperties
import finance.domain.Transaction
import finance.processors.InsertTransactionProcessor
import finance.processors.StringTransactionProcessor
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class TransactionToDatabaseRouteBuilder @Autowired constructor(
        private var stringTransactionProcessor: StringTransactionProcessor,
        private var insertTransactionProcessor: InsertTransactionProcessor,
        private var camelProperties: CamelProperties
) : RouteBuilder() {


    @Throws(Exception::class)
    override fun configure() {

        onException(Exception::class.java)
                .handled(true)
                .log(LoggingLevel.INFO, "major failure in the TransactionToDatabaseRoute.")
                .end()

        from(camelProperties.transactionToDatabaseRoute)
                .autoStartup(camelProperties.autoStartRoute)
                .routeId(camelProperties.transactionToDatabaseRouteId)
                .split(body())
                .log(LoggingLevel.INFO, "split body completed.")
                .convertBodyTo(Transaction::class.java)
                .log(LoggingLevel.INFO, "converted body to string.")
                .process(stringTransactionProcessor)
                .convertBodyTo(String::class.java)
                .to(camelProperties.jsonFileWriterRoute)
                .process(insertTransactionProcessor)
                .log(LoggingLevel.INFO, "message was processed by insertTransactionProcessor.")
                .end()
    }
}