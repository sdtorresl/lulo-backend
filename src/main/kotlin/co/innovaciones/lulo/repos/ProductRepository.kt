package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.domain.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface ProductRepository : JpaRepository<Product, Long> {

    fun findAllById(id: Long?, pageable: Pageable): Page<Product>

    fun existsByPictureId(id: Long?): Boolean

    fun findFirstByPicture(`file`: File): Product?

}
