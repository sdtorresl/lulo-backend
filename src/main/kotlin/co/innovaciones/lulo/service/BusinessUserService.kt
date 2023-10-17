package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.BusinessUserDTO
import co.innovaciones.lulo.model.SimplePage
import org.springframework.data.domain.Pageable


interface BusinessUserService {

    fun findAll(filter: String?, pageable: Pageable): SimplePage<BusinessUserDTO>

    fun `get`(id: Long): BusinessUserDTO

    fun create(businessUserDTO: BusinessUserDTO): Long

    fun update(id: Long, businessUserDTO: BusinessUserDTO)

    fun delete(id: Long)

    fun profilePictureExists(id: Long?): Boolean

    fun userExists(id: Long?): Boolean

}
