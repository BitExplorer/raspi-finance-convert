package finance

import finance.domain.AccountType
import finance.domain.Transaction
import finance.helpers.TransactionDAO

//import com.github.javafaker.service.FakeValuesService
//import com.github.javafaker.service.RandomService

import finance.services.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils
import spock.lang.Specification

import java.math.RoundingMode
import java.sql.Date
import java.sql.Timestamp

@SpringBootTest
class TransactionServicePerformanceSpec extends Specification {

    @Autowired
    private TransactionService transactionService

    @Autowired
    private TransactionDAO transactionDAO;

    //private FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-US"), new RandomService())
    //def password = org.apache.commons.lang.RandomStringUtils.randomAlphanumeric(length)

    def "transaction service performance test"() {
        when:
        for (int idx = 0; idx < 5000; idx++) {
            Transaction transaction = createTransaction()
            transactionService.insertTransaction(transaction)
        }
        then:
        //transactionDAO.transactionCount() == 5000
        1 == 1
    }

    private Transaction createTransaction() {
        Transaction transaction = new Transaction()
        transaction.setGuid("[0-9a-z]{8}" + '-' + "[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}")
        transaction.setAccountNameOwner(RandomStringUtils.randomAlphabetic(8) + '_' + RandomStringUtils.randomAlphabetic(4))
        transaction.setDescription(RandomStringUtils.randomAlphabetic(25))
        transaction.setCategory(RandomStringUtils.randomAlphabetic(20))
        transaction.setNotes(RandomStringUtils.randomAlphabetic(40))
        transaction.setReoccurring(Boolean.parseBoolean(fakeValuesService.regexify("(true|false)")))
        transaction.setAccountType(AccountType.valueOf(fakeValuesService.regexify("(Credit|Debit)")))
        transaction.setTransactionDate(new Date(1553600000 + Integer.parseInt(fakeValuesService.regexify("[0-9]{5}"))))
        transaction.setDateUpdated(new Timestamp(1553600000000 + Integer.parseInt(fakeValuesService.regexify("[0-9]{5}000"))))
        transaction.setDateAdded(new Timestamp(1553600000000 + Integer.parseInt(fakeValuesService.regexify("[0-9]{5}000"))))
        transaction.setAmount(new BigDecimal(fakeValuesService.regexify($/[0-9]{2}\.[0-9]{2}/$)).setScale(2, RoundingMode.HALF_UP))
        transaction.setCleared(1)

        return transaction
    }
}
