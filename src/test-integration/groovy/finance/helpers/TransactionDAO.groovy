package finance.helpers

import lombok.val
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

class TransactionDAO {

    private static String SQL_TRUNCATE_TRANSACTION_TABLE = "TRUNCATE TABLE t_transaction CASCADE"
    private static String SQL_DROP_TRANSACTION_TABLE = "DROP TABLE t_transaction"
    private static String SQL_TRUNCATE_ACCOUNT_TABLE = "TRUNCATE TABLE t_account CASCADE"
    private static String SQL_DROP_ACCOUNT_TABLE = "DROP TABLE t_account"
    private static String SQL_TRUNCATE_CATEGORY_TABLE = "TRUNCATE TABLE t_category CASCADE"
    private static String SQL_DROP_CATEGORY_TABLE = "DROP TABLE t_category"
    private static String SQL_TRUNCATE_TRANSACTION_CATEGORIES_TABLE = "TRUNCATE TABLE t_transaction_categories"
    private static String SQL_DROP_TRANSACTION_CATEGORIES_TABLE = "DROP TABLE t_transaction_categories"

    private JdbcTemplate jdbcTemplate

    @Autowired
    public TransactionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate
    }

    public void truncateTransactionTable() {
        try {
            jdbcTemplate.execute(SQL_TRUNCATE_TRANSACTION_TABLE)
        } catch (Exception e) {
            println("failure = ${SQL_TRUNCATE_TRANSACTION_TABLE}")
        }
    }

    public void dropTransactionTable() {
        jdbcTemplate.execute(SQL_DROP_TRANSACTION_TABLE)
    }

    public void truncateTransactionCategories() {
        jdbcTemplate.execute(SQL_TRUNCATE_TRANSACTION_CATEGORIES_TABLE)
    }

    public void dropTransactionCategories() {
        jdbcTemplate.execute(SQL_DROP_TRANSACTION_CATEGORIES_TABLE)
    }

    public void dropAccountTable() {
        jdbcTemplate.execute(SQL_DROP_ACCOUNT_TABLE)
    }

    public void truncateAccountTable() {
        try {
            jdbcTemplate.execute(SQL_TRUNCATE_ACCOUNT_TABLE)
        } catch (Exception e) {
            println("failure = ${SQL_TRUNCATE_TRANSACTION_TABLE}")
        }
    }

    public void dropCategoryTable() {
        jdbcTemplate.execute(SQL_DROP_CATEGORY_TABLE)
    }

    public void truncateCategoryTable() {
        try {
            jdbcTemplate.execute(SQL_TRUNCATE_CATEGORY_TABLE)
        } catch (Exception e) {
            println("failure = ${SQL_TRUNCATE_TRANSACTION_TABLE}")
        }
    }

    public int transactionCount() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_transaction", Integer.class)
    }

    public int transactionCategoriesCount() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_transaction_categories", Integer.class)
    }

}
