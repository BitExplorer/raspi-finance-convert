package finance.repositories

import finance.models.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.transaction.Transactional

@Repository
interface CategoryRepository<T : Category> : JpaRepository<T, Long> {
    fun findByCategory(category: String): Optional<Category>
}