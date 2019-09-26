package finance.repositories

import finance.models.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


//@Repository
//interface TransactionRepository : JpaRepository<Transaction, Long> {
//    //TODO: add LIMIT 1 result
//    fun findByGuid(guid: String): Optional<Transaction>
//}

@Repository
interface TransactionRepository<T : Transaction> : JpaRepository<T, Long> {
    //TODO: add LIMIT 1 result
    fun findByGuid(guid: String): Optional<Transaction>
}
