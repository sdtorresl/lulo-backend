package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.Category
import co.innovaciones.lulo.model.CategoryDTO
import co.innovaciones.lulo.model.SimplePage
import co.innovaciones.lulo.repos.BusinessRepository
import co.innovaciones.lulo.repos.CategoryRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.repos.ProductRepository
import co.innovaciones.lulo.util.NotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
@Transactional
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val fileRepository: FileRepository,
    private val businessRepository: BusinessRepository
) : CategoryService {

    override fun findAll(filter: String?, pageable: Pageable): SimplePage<CategoryDTO> {
        var page: Page<Category>
        if (filter != null) {
            page = categoryRepository.findAllById(filter.toLongOrNull(), pageable)
        } else {
            page = categoryRepository.findAll(pageable)
        }
        return SimplePage(page.content
                .stream()
                .map { category -> mapToDTO(category, CategoryDTO()) }
                .toList(),
                page.totalElements, pageable)
    }

    override fun `get`(id: Long): CategoryDTO = categoryRepository.findById(id)
            .map { category -> mapToDTO(category, CategoryDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(categoryDTO: CategoryDTO): Long {
        val category = Category()
        mapToEntity(categoryDTO, category)
        return categoryRepository.save(category).id!!
    }

    override fun update(id: Long, categoryDTO: CategoryDTO) {
        val category = categoryRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(categoryDTO, category)
        categoryRepository.save(category)
    }

    override fun delete(id: Long) {
        categoryRepository.deleteById(id)
    }

    private fun mapToDTO(category: Category, categoryDTO: CategoryDTO): CategoryDTO {
        categoryDTO.id = category.id
        categoryDTO.name = category.name
        categoryDTO.type = category.type
        categoryDTO.products = category.products?.stream()
                ?.map { product -> product.id!! }
                ?.toList()
        categoryDTO.file = category.file?.id
        categoryDTO.business = category.business?.id
        return categoryDTO
    }

    private fun mapToEntity(categoryDTO: CategoryDTO, category: Category): Category {
        category.name = categoryDTO.name
        category.type = categoryDTO.type
        val products = productRepository.findAllById(categoryDTO.products ?: emptyList())
        if (products.size != (if (categoryDTO.products == null) 0 else
                categoryDTO.products!!.size)) {
            throw NotFoundException("one of products not found")
        }
        category.products = products.toMutableSet()
        val file = if (categoryDTO.file == null) null else
                fileRepository.findById(categoryDTO.file!!)
                .orElseThrow { NotFoundException("file not found") }
        category.file = file
        val business = if (categoryDTO.business == null) null else
                businessRepository.findById(categoryDTO.business!!)
                .orElseThrow { NotFoundException("business not found") }
        category.business = business
        return category
    }

    override fun fileExists(id: Long?): Boolean = categoryRepository.existsByFileId(id)

}
