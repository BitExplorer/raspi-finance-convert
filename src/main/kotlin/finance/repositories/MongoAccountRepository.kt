package finance.repositories

import finance.models.Account
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

@Profile("mongo")
interface MongoAccountRepository<T : Account> : MongoRepository<T, Long> {
    fun findByAccountNameOwner(accountNameOwner: String): Optional<Account>
}