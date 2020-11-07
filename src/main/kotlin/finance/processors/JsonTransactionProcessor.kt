package finance.processors

import com.fasterxml.jackson.databind.ObjectMapper
import finance.domain.Transaction
import finance.services.TransactionService
import io.micrometer.core.annotation.Timed
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolation

import javax.validation.Validator


@Component
class JsonTransactionProcessor @Autowired constructor(private val validator: Validator) : Processor {
    //private val logger = LoggerFactory.getLogger(javaClass)

    @Throws(Exception::class)
    @Timed("json.transaction.processor.timer")
    override fun process(exchange: Exchange) {
        logger.info("JsonTransactionProcessor called")

        val message = exchange.`in`
        val payload = message.getBody(String::class.java)
        val transactions = mapper.readValue(payload, Array<Transaction>::class.java)
        logger.info(transactions.toString())
        for (transaction in transactions) {
            //val constraintViolations: Set<ConstraintViolation<Transaction>> = validator.validate(transaction)
            //if (constraintViolations.isNotEmpty()) {
                //TODO: handle the violation

                TransactionService.logger.info("validation issue for<${transaction}>")
                //meterService.incrementErrorCounter(transaction.accountNameOwner, MeterService.ErrorType.VALIDATION_ERROR)
                TransactionService.logger.info("METRIC_TRANSACTION_VALIDATOR_FAILED_COUNTER")
            //}
        }
        logger.info("JsonTransactionProcessor size: ${transactions.size}")
        message.body = transactions
    }

    companion object {
        val mapper = ObjectMapper()
        val logger: Logger
            get() = LoggerFactory.getLogger(JsonTransactionProcessor::class.java)
    }
}
