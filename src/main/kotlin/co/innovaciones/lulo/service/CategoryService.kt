package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.CategoryDTO
import co.innovaciones.lulo.model.SimplePage
import org.springframework.data.domain.Pageable


interface CategoryService {

    fun findAll(filter: String?, pageable: Pageable): SimplePage<CategoryDTO>

    fun `get`(id: Long): CategoryDTO

    fun create(categoryDTO: CategoryDTO): Long

    fun update(id: Long, categoryDTO: CategoryDTO)

    fun delete(id: Long)

    fun fileExists(id: Long?): Boolean

}
