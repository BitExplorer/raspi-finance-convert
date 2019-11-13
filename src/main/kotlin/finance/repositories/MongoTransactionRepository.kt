package finance.repositories

import finance.models.Transaction
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Profile("mongo")
interface MongoTransactionRepository<T : Transaction> : MongoRepository<T, Long> {
    fun findByGuid(guid: String): Optional<Transaction>

//    @Modifying
//    @Query("UPDATE TransactionEntity set amount = ?1 WHERE guid = ?2")
//    @Transactional
//    fun setAmountByGuid(amount: BigDecimal, guild: String)
//
//    @Modifying
//    @Query("UPDATE TransactionEntity set cleared = ?1 WHERE guid = ?2")
//    @Transactional
//    fun setClearedByGuid(cleared: Int, guild: String)

}