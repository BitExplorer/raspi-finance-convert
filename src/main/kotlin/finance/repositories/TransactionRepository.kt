package finance.repositories

import finance.domain.Transaction
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Profile("!mongo")
@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
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
