package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.PetDTO
import co.innovaciones.lulo.model.SimplePage
import org.springframework.data.domain.Pageable


interface PetService {

    fun findAll(filter: String?, pageable: Pageable): SimplePage<PetDTO>

    fun `get`(id: Long): PetDTO

    fun create(petDTO: PetDTO): Long

    fun update(id: Long, petDTO: PetDTO)

    fun delete(id: Long)

    fun profilePictureExists(id: Long?): Boolean

}
