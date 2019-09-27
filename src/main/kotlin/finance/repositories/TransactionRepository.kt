package finance.repositories

import finance.models.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


//@Repository
//interface TransactionRepository : JpaRepository<Transaction, Long> {
//    //TODO: add LIMIT 1 result
//    fun findByGuid(guid: String): Optional<Transaction>
//}

@Repository
interface TransactionRepository<T : Transaction> : JpaRepository<T, Long> {
    //TODO: add LIMIT 1 result
    fun findByGuid(guid: String): Optional<Transaction>

    @Modifying
    @Query("UPDATE TransactionEntity set amount = ?1 WHERE guid = ?2")
    @Transactional
    fun setAmountByGuid(amount: BigDecimal, guild: String)

    @Modifying
    @Query("UPDATE TransactionEntity set cleared = ?1 WHERE guid = ?2")
    @Transactional
    fun setClearedByGuid(cleared: Int, guild: String)

}
