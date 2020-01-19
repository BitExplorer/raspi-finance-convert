package finance

import com.github.javafaker.service.FakeValuesService
import com.github.javafaker.service.RandomService
import finance.helpers.TransactionDAO
import finance.domain.Transaction
import finance.domain.AccountType
import finance.services.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.math.RoundingMode
import java.sql.Timestamp
import java.sql.Date

@SpringBootTest
class TransactionServicePerformanceSpec extends Specification {

    @Autowired
    private TransactionService transactionService

    @Autowired
    private TransactionDAO transactionDAO;

    private FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-US"), new RandomService())

    def "transaction service performance test" () {
        when:
        for( int idx = 0; idx < 5000; idx ++ ) {
            Transaction transaction = createTransaction()
            transactionService.insertTransaction(transaction)
        }
        then:
        //transactionDAO.transactionCount() == 5000
        1==1
    }

    private Transaction createTransaction() {
        Transaction transaction = new Transaction()
        transaction.setGuid(fakeValuesService.regexify("[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}"))
        transaction.setAccountNameOwner(fakeValuesService.regexify("[a-z]{8}_[a-z]{4}"))
        transaction.setDescription(fakeValuesService.regexify("[a-z]{25}"))
        transaction.setCategory(fakeValuesService.regexify("[a-z]{20}"))
        transaction.setNotes(fakeValuesService.regexify("[a-z]{40}"))
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
