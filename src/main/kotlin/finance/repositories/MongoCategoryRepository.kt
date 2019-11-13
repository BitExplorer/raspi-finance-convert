package finance.repositories

import finance.models.Category
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

@Profile("mongo")
interface MongoCategoryRepository<T : Category> : MongoRepository<T, Long> {
    fun findByCategory(category: String): Optional<Category>
}