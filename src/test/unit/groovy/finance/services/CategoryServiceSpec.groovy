package finance.services

import finance.domain.Category
import finance.repositories.CategoryRepository
import io.micrometer.core.instrument.MeterRegistry
import spock.lang.Specification

class CategoryServiceSpec extends Specification {

    CategoryRepository categoryRepository = Mock(CategoryRepository)
    MeterService meterService = Mock(MeterService)
    CategoryService categoryService = new CategoryService(categoryRepository, meterService)

    def "test findByCategory"() {
        given:
        Category category = new Category()
        category.category = "restaurant"

        when:
        categoryService.findByCategory("resturants")

        then:
        1 * categoryRepository.findByCategory('resturants') >> Optional.of(category)
        0 * _
    }
}
