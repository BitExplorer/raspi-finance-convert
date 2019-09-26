package finance.services

import finance.models.Category
import finance.repositories.CategoryRepository
import finance.utils.Constants.METRIC_DUPLICATE_CATEGORY_INSERT_ATTEMPT_COUNTER
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.Optional.empty
import javax.transaction.Transactional

@Service
open class CategoryService @Autowired constructor(
        private var categoryRepository: CategoryRepository<Category>,
        private var meterRegistry: MeterRegistry
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    //@Transactional
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
        } catch ( e: JdbcSQLIntegrityConstraintViolationException) {
            meterRegistry.counter(METRIC_DUPLICATE_CATEGORY_INSERT_ATTEMPT_COUNTER).increment()
            logger.info("categoryRepository.saveAndFlush(category) - JdbcSQLIntegrityConstraintViolationException")
            return false
        }

        return true
    }
}
