package finance.repositories

import finance.models.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

//TODO: is invariant
@Repository
interface AccountRepository<T : Account> : JpaRepository<T, Long> {
    fun findByAccountNameOwner(accountNameOwner: String): Optional<Account>
}
