package finance.services

import com.fasterxml.jackson.databind.ObjectMapper
import finance.helpers.TransactionDAO
import finance.models.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class TransactionServicesSpec extends Specification {

    def jsonPayload = "{\"guid\":\"0a23fec3-18c8-4b89-a5af-68fab8db8620\",\"accountType\":\"credit\",\"accountNameOwner\":\"amex_brian\",\"transactionDate\":1475647200000,\"description\":\"Cafe Roale\",\"category\":\"online\",\"amount\":33.08,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1475588992000,\"dateAdded\":1475588992000,\"sha256\":\"\"}"
    def jsonPayload1 = "{\"guid\":\"680cf64e-147d-4404-84e4-db018bab9d1a\",\"accountType\":\"credit\",\"accountNameOwner\":\"rcard_brian\",\"transactionDate\":1525154400000,\"description\":\"Target - Coon Rapids\",\"category\":\"target\",\"amount\":5.21,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1525395023000,\"dateAdded\":1525395023000,\"sha256\":\"\"}"
    def jsonPayload2 = "{\"guid\":\"e5476b4e-6c7b-4fc5-a558-45d006ca6d97\",\"accountType\":\"credit\",\"accountNameOwner\":\"chase_kari\",\"transactionDate\":1328767200000,\"description\":\"SuperAmerica\",\"category\":\"fuel\",\"amount\":25.00,\"cleared\":1,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1487301459000,\"dateAdded\":1487301459000,\"sha256\":\"\"}"
    def jsonPayload3 = "{\"guid\":\"ffda166a-a1c4-408b-a745-6ea39000fd1e\",\"accountType\":\"credit\",\"accountNameOwner\":\"discover_brian\",\"transactionDate\":1513058400000,\"description\":\"amazon.com\",\"category\":\"online\",\"amount\":0.99,\"cleared\":1,\"reoccurring\":false,\"notes\":\"eGiftcard *9922\",\"dateUpdated\":1513213180000,\"dateAdded\":1513213180000,\"sha256\":\"\"}"
    def jsonPayload4 = "{\"guid\":\"ffda166a-a1c4-408b-a745-6ea39000f667\",\"accountType\":\"credit\",\"accountNameOwner\":\"discover_brian\",\"transactionDate\":1513058300000,\"description\":\"aliexpress.com\",\"category\":\"online\",\"amount\":1.99,\"cleared\":1,\"reoccurring\":false,\"notes\":\"eGiftcard *9922\",\"dateUpdated\":1513213180000,\"dateAdded\":1513213180000,\"sha256\":\"\"}"
    //def jsonPayload5 = "{\"transactionId\":0,\"guid\":\"7adfb636-60f5-4eda-ab10-4cbde124a358\",\"accountId\":0,\"accountType\":\"credit\",\"accountNameOwner\":\"amex_newegg_brian\",\"transactionDate\":1547013600000,\"description\":\"Amazon.com\",\"category\":\"online\",\"amount\":1.00,\"cleared\":0,\"reoccurring\":false,\"notes\":\"\",\"dateUpdated\":1547088223000,\"dateAdded\":1547088223000,\"sha256\":\"a690f5c98a910493746f05e2ee812e1bfed8231e8cc040defdfaec9114e20621\"}"

    @Autowired
    TransactionService transactionService

    @Autowired
    TransactionDAO transactionDAO

    private static ObjectMapper mapper = new ObjectMapper()

    def "test insert valid transactions"() {
        transactionDAO.truncateTransactionTable()
        transactionDAO.truncateAccountTable()
        transactionDAO.truncateCategoryTable()

        given:
        Transaction transaction = mapper.readValue(jsonPayload, Transaction.class)
        Transaction transaction1 = mapper.readValue(jsonPayload1, Transaction.class)
        Transaction transaction2 = mapper.readValue(jsonPayload2, Transaction.class)
        Transaction transaction3 = mapper.readValue(jsonPayload3, Transaction.class)
        Transaction transaction4 = mapper.readValue(jsonPayload4, Transaction.class)

        when:
        Boolean result = transactionService.insertTransaction(transaction)
        Boolean result1 = transactionService.insertTransaction(transaction1)
        Boolean result2 = transactionService.insertTransaction(transaction2)
        Boolean result0 = transactionService.insertTransaction(transaction2)
        Boolean result3 = transactionService.insertTransaction(transaction3)
        Boolean result4 = transactionService.insertTransaction(transaction4)

        then:
        result.is(true)
        result1.is(true)
        result2.is(true)
        result0.is(false)
        result3.is(true)
        result4.is(true)
        transactionDAO.transactionCount() == 5
        transactionDAO.transactionCategoriesCount() == 5
    }
}
