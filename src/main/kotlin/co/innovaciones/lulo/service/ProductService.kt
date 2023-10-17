package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.ProductDTO
import co.innovaciones.lulo.model.SimplePage
import org.springframework.data.domain.Pageable


interface ProductService {

    fun findAll(filter: String?, pageable: Pageable): SimplePage<ProductDTO>

    fun `get`(id: Long): ProductDTO

    fun create(productDTO: ProductDTO): Long

    fun update(id: Long, productDTO: ProductDTO)

    fun delete(id: Long)

    fun pictureExists(id: Long?): Boolean

    fun getReferencedWarning(id: Long): String?

}
