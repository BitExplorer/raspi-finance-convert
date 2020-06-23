package finance.services

import finance.domain.Category
import finance.repositories.CategoryRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.Optional.empty

@Service
class CategoryService @Autowired constructor(
        private val categoryRepository: CategoryRepository,
        private val meterService: MeterService
) {
    //@Timed("find.by.category.timer")
    fun findByCategory( category: String ): Optional<Category> {
        logger.debug("findByCategory")
        val categoryOptional: Optional<Category> = categoryRepository.findByCategory(category)
        if( categoryOptional.isPresent ) {
            return categoryOptional
        }
        return empty()
    }

    //TODO: which @Transactional is the one to utilize
    //@Timed("insert.category.timer")
    fun insertCategory(category: Category) : Boolean {
        logger.debug("insertAccount")

        try {
            categoryRepository.saveAndFlush(category)
        } catch ( e: Exception) {
            //meterRegistry.counter(METRIC_DUPLICATE_CATEGORY_INSERT_ATTEMPT_COUNTER).increment()
            logger.info("categoryRepository.saveAndFlush(category) - JdbcSQLIntegrityConstraintViolationException")
            return false
        }

        return true
    }

    companion object {
        val logger : Logger
            get() = LoggerFactory.getLogger(CategoryService::class.java)
    }
}
