package finance.processors

import com.fasterxml.jackson.databind.ObjectMapper
import finance.domain.Transaction
import io.micrometer.core.annotation.Timed
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
open class JsonTransactionProcessor : Processor {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(Exception::class)
    @Timed("json.transaction.processor.timer")
    override fun process(exchange: Exchange) {
        logger.debug("JsonTransactionProcessor called")
        val message = exchange.`in`
        val payload = message.getBody(String::class.java)
        val transactions = mapper.readValue(payload, Array<Transaction>::class.java)
        logger.info("JsonTransactionProcessor size: ${transactions.size}")
        message.body = transactions
    }

    companion object {
        val mapper = ObjectMapper()
    }
}
