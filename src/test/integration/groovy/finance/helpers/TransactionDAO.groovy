package finance.helpers

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
    private static String SQL_COUNT_TRANSACTIONS = "SELECT COUNT(*) FROM t_transaction"
    private static String SQL_COUNT_TRANSACTION_CATEGORIES = "SELECT COUNT(*) FROM t_transaction_categories"

    private JdbcTemplate jdbcTemplate

    @Autowired
    TransactionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate
    }

    void truncateTransactionTable() {
        jdbcTemplate.execute(SQL_TRUNCATE_TRANSACTION_TABLE)
    }

    void dropTransactionTable() {
        jdbcTemplate.execute(SQL_DROP_TRANSACTION_TABLE)
    }

    public void truncateTransactionCategories() {
        jdbcTemplate.execute(SQL_TRUNCATE_TRANSACTION_CATEGORIES_TABLE)
    }

    void dropTransactionCategories() {
        jdbcTemplate.execute(SQL_DROP_TRANSACTION_CATEGORIES_TABLE)
    }

    void dropAccountTable() {
        jdbcTemplate.execute(SQL_DROP_ACCOUNT_TABLE)
    }

    void truncateAccountTable() {
        jdbcTemplate.execute(SQL_TRUNCATE_ACCOUNT_TABLE)
    }

    void dropCategoryTable() {
        jdbcTemplate.execute(SQL_DROP_CATEGORY_TABLE)
    }

    void truncateCategoryTable() {
        jdbcTemplate.execute(SQL_TRUNCATE_CATEGORY_TABLE)
    }

    int transactionCount() {
        return jdbcTemplate.queryForObject(
                SQL_COUNT_TRANSACTIONS, Integer.class)
    }

    int transactionCategoriesCount() {
        return jdbcTemplate.queryForObject(
                SQL_COUNT_TRANSACTION_CATEGORIES, Integer.class)
    }
}
