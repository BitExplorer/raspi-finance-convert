package finance.repositories

import finance.domain.Category
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Profile("!mongo")
@Repository
interface CategoryRepository<T : Category> : JpaRepository<T, Long> {
    fun findByCategory(category: String): Optional<Category>
}