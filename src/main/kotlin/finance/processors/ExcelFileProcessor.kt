package finance.processors

import com.fasterxml.jackson.databind.ObjectMapper
import finance.domain.Transaction
import finance.services.ExcelFileService
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!mongo")
@Component
open class ExcelFileProcessor  @Autowired constructor(private var excelFileService: ExcelFileService)
    : Processor {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Throws(Exception::class)
    override fun process(exchange: Exchange) {
        val message = exchange.`in`
        val inputExcelFileName = message.getBody(String::class.java)
        val payload = excelFileService.processProtectedExcelFile(inputExcelFileName)
        //mapper.readValue()
        //val transactions = mapper.readValue(payload, String::class.java)
        //message.body = transactions
    }
    companion object {
        val mapper = ObjectMapper()
    }
}