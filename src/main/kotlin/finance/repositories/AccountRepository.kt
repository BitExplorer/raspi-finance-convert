package finance.repositories

import finance.domain.Account
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Profile("!mongo")
@Repository
interface AccountRepository : JpaRepository<Account, Long> {
    fun findByAccountNameOwner(accountNameOwner: String): Optional<Account>
}
