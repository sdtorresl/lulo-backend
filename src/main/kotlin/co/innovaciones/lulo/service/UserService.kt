package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.SimplePage
import co.innovaciones.lulo.model.UserDTO
import org.springframework.data.domain.Pageable


interface UserService {

    fun findAll(filter: String?, pageable: Pageable): SimplePage<UserDTO>

    fun `get`(id: Long): UserDTO

    fun create(userDTO: UserDTO): Long

    fun update(id: Long, userDTO: UserDTO)

    fun delete(id: Long)

    fun usernameExists(username: String?): Boolean

    fun emailExists(email: String?): Boolean

    fun getReferencedWarning(id: Long): String?

}
