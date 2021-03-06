package finance.processors

import finance.domain.Transaction
import io.micrometer.core.annotation.Timed
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class StringTransactionProcessor : Processor {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(Exception::class)
    @Timed("string.transaction.processor.timer")
    override fun process(exchange: Exchange) {
        logger.info("StringTransactionProcessor called.")
        val message = exchange.`in`
        val transaction = message.getBody(Transaction::class.java)
        logger.info("transaction.guid=${transaction.guid}")
        exchange.setProperty("guid", transaction.guid)
        message.body = transaction.toString()
        logger.info("StringTransactionProcessor completed")
    }

    companion object {
        val logger: Logger
            get() = LoggerFactory.getLogger(StringTransactionProcessor::class.java)
    }
}
