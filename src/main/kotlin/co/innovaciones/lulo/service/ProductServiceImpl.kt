package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.Product
import co.innovaciones.lulo.model.ProductDTO
import co.innovaciones.lulo.model.SimplePage
import co.innovaciones.lulo.repos.CategoryRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.repos.OrderItemRepository
import co.innovaciones.lulo.repos.ProductRepository
import co.innovaciones.lulo.util.NotFoundException
import co.innovaciones.lulo.util.WebUtils
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val fileRepository: FileRepository,
    private val categoryRepository: CategoryRepository,
    private val orderItemRepository: OrderItemRepository
) : ProductService {

    override fun findAll(filter: String?, pageable: Pageable): SimplePage<ProductDTO> {
        var page: Page<Product>
        if (filter != null) {
            page = productRepository.findAllById(filter.toLongOrNull(), pageable)
        } else {
            page = productRepository.findAll(pageable)
        }
        return SimplePage(page.content
                .stream()
                .map { product -> mapToDTO(product, ProductDTO()) }
                .toList(),
                page.totalElements, pageable)
    }

    override fun `get`(id: Long): ProductDTO = productRepository.findById(id)
            .map { product -> mapToDTO(product, ProductDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(productDTO: ProductDTO): Long {
        val product = Product()
        mapToEntity(productDTO, product)
        return productRepository.save(product).id!!
    }

    override fun update(id: Long, productDTO: ProductDTO) {
        val product = productRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(productDTO, product)
        productRepository.save(product)
    }

    override fun delete(id: Long) {
        val product = productRepository.findById(id)
                .orElseThrow { NotFoundException() }
        // remove many-to-many relations at owning side
        categoryRepository.findAllByProducts(product)
                .forEach { category -> category.products!!.remove(product) }
        productRepository.delete(product)
    }

    private fun mapToDTO(product: Product, productDTO: ProductDTO): ProductDTO {
        productDTO.id = product.id
        productDTO.name = product.name
        productDTO.description = product.description
        productDTO.price = product.price
        productDTO.quantity = product.quantity
        productDTO.available = product.available
        productDTO.picture = product.picture?.id
        return productDTO
    }

    private fun mapToEntity(productDTO: ProductDTO, product: Product): Product {
        product.name = productDTO.name
        product.description = productDTO.description
        product.price = productDTO.price
        product.quantity = productDTO.quantity
        product.available = productDTO.available
        val picture = if (productDTO.picture == null) null else
                fileRepository.findById(productDTO.picture!!)
                .orElseThrow { NotFoundException("picture not found") }
        product.picture = picture
        return product
    }

    override fun pictureExists(id: Long?): Boolean = productRepository.existsByPictureId(id)

    override fun getReferencedWarning(id: Long): String? {
        val product = productRepository.findById(id)
                .orElseThrow { NotFoundException() }
        val productsCategory = categoryRepository.findFirstByProducts(product)
        if (productsCategory != null) {
            return WebUtils.getMessage("product.category.products.referenced", productsCategory.id)
        }
        val productOrderItem = orderItemRepository.findFirstByProduct(product)
        if (productOrderItem != null) {
            return WebUtils.getMessage("product.orderItem.product.referenced", productOrderItem.id)
        }
        return null
    }

}
