package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.Business
import co.innovaciones.lulo.domain.Category
import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.domain.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface CategoryRepository : JpaRepository<Category, Long> {

    fun findAllById(id: Long?, pageable: Pageable): Page<Category>

    fun findFirstByBusiness(business: Business): Category?

    fun findAllByProducts(product: Product): List<Category>

    fun findFirstByProducts(product: Product): Category?

    fun findFirstByFile(`file`: File): Category?

    fun existsByFileId(id: Long?): Boolean

}
