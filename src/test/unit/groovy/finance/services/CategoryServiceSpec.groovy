package finance.services

import finance.domain.Category
import finance.repositories.CategoryRepository
import spock.lang.Specification

class CategoryServiceSpec extends Specification {

    CategoryRepository mockCategoryRepository = Mock(CategoryRepository)
    MeterService mockMeterService = Mock(MeterService)
    CategoryService categoryService = new CategoryService(mockCategoryRepository, mockMeterService)

    def "test findByCategory"() {
        given:
        Category category = new Category()
        category.category = "restaurant"

        when:
        categoryService.findByCategory("resturants")

        then:
        1 * mockCategoryRepository.findByCategory('resturants') >> Optional.of(category)
        0 * _
    }
}
