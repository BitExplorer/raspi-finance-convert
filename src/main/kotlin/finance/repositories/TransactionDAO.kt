package finance.repositories

import finance.utils.TransactionBuilder
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

//@EnableScheduling
@Component
open class TransactionDAO @Autowired constructor(
        private var jdbcTemplate: JdbcTemplate
)  {
    private val SQL_TRUNCATE_TRANSACTION_TABLE = "TRUNCATE TABLE t_transaction CASCADE"
    private val SQL_DROP_TRANSACTION_TABLE = "DROP TABLE t_transaction"
    private val SQL_TRUNCATE_ACCOUNT_TABLE = "TRUNCATE TABLE t_account CASCADE"
    private val SQL_DROP_ACCOUNT_TABLE = "DROP TABLE t_account"
    private val SQL_TRUNCATE_CATEGORY_TABLE = "TRUNCATE TABLE t_category CASCADE"
    private val SQL_DROP_CATEGORY_TABLE = "DROP TABLE t_category"
    private val SQL_TRUNCATE_TRANSACTION_CATEGORIES_TABLE = "TRUNCATE TABLE t_transaction_categories"
    private val SQL_DROP_TRANSACTION_CATEGORIES_TABLE = "DROP TABLE t_transaction_categories"

//    @Scheduled(fixedDelay = Long.MAX_VALUE)
//    @Timed
//    fun sqlCalls() {
//        println("sqlCalls" )
//        jdbcTemplate.execute(this.SQL_CREATE_TABLE)
//        var transaction = TransactionBuilder().buildTransaction()
//        println(transaction)
//    }

    fun truncateTransactionTable() {
        try {
            jdbcTemplate.execute(this.SQL_TRUNCATE_TRANSACTION_TABLE)
        } catch ( e: Exception ) {
            println("failure = ${this.SQL_TRUNCATE_TRANSACTION_TABLE}")
        }
    }

    fun dropTransactionTable() {
        jdbcTemplate.execute(this.SQL_DROP_TRANSACTION_TABLE)
    }

    fun truncateTransactionCategories() {
        jdbcTemplate.execute(this.SQL_TRUNCATE_TRANSACTION_CATEGORIES_TABLE)
    }

    fun dropTransactionCategories() {
        jdbcTemplate.execute(this.SQL_DROP_TRANSACTION_CATEGORIES_TABLE)
    }

    fun dropAccountTable() {
        jdbcTemplate.execute(this.SQL_DROP_ACCOUNT_TABLE)
    }

    fun truncateAccountTable() {
        try {
            jdbcTemplate.execute(this.SQL_TRUNCATE_ACCOUNT_TABLE)
        } catch ( e: Exception ) {
            println("failure = ${this.SQL_TRUNCATE_TRANSACTION_TABLE}")
        }
    }

    fun dropCategoryTable() {
        jdbcTemplate.execute(this.SQL_DROP_CATEGORY_TABLE)
    }

    fun truncateCategoryTable() {
        try {
            jdbcTemplate.execute(this.SQL_TRUNCATE_CATEGORY_TABLE)
        } catch ( e: Exception ) {
            println("failure = ${this.SQL_TRUNCATE_TRANSACTION_TABLE}")
        }
    }

    fun transactionCount() : Int {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_transaction", Int::class.java)!!
    }

    fun transactionCategoriesCount() : Int {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_transaction_categories", Int::class.java)!!
    }
}