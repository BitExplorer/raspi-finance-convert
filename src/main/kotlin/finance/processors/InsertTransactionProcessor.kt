package finance.processors

import com.fasterxml.jackson.databind.ObjectMapper
import finance.models.Transaction
import finance.services.TransactionService
import finance.utils.Constants.METRIC_INSERT_TRANSACTION_TIMER
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!mongo")
@Component
open class InsertTransactionProcessor  @Autowired constructor(
        private var transactionService: TransactionService,
        private var meterRegistry: MeterRegistry

) :Processor {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(Exception::class)
    @Timed("insert.transaction.processor.timer")
    override fun process(exchange: Exchange) {
        val message = exchange.`in`
        val payload  = message.getBody(String::class.java)
        logger.info("payload=$payload")
        val transaction = mapper.readValue(payload, Transaction::class.java)
        logger.info("will call to insertTransaction(), guid=${transaction.guid} description=${transaction.description}")

//        meterRegistry.timer(METRIC_INSERT_TRANSACTION_TIMER).record {
//            transactionService.insertTransaction(transaction)
//        }
        transactionService.insertTransaction(transaction)

        logger.info("called to insertTransaction(), guid=${transaction.guid} description=${transaction.description}")
        message.body = transaction.toString()
        logger.debug("InsertTransactionProcessor completed")
    }

    companion object {
        val mapper = ObjectMapper()
    }
}
