package finance.processors

import finance.services.ExcelFileService
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!mongo")
@Component
open class ExcelFileProcessor  @Autowired constructor(private var excelFileService: ExcelFileService) : Processor {

    @Throws(Exception::class)
    override fun process(exchange: Exchange) {
        val message = exchange.`in`
        val inputExcelFileName = message.getBody(String::class.java)
        excelFileService.processProtectedExcelFile(inputExcelFileName)
    }
}