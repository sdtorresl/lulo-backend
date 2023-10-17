package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.BusinessDTO
import co.innovaciones.lulo.model.SimplePage
import org.springframework.data.domain.Pageable


interface BusinessService {

    fun findAll(filter: String?, pageable: Pageable): SimplePage<BusinessDTO>

    fun `get`(id: Long): BusinessDTO

    fun create(businessDTO: BusinessDTO): Long

    fun update(id: Long, businessDTO: BusinessDTO)

    fun delete(id: Long)

    fun logoExists(id: Long?): Boolean

    fun getReferencedWarning(id: Long): String?

}
