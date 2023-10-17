package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface UserRepository : JpaRepository<User, Long> {

    fun findByUsernameIgnoreCase(username: String): User?

    fun findAllById(id: Long?, pageable: Pageable): Page<User>

    fun existsByUsernameIgnoreCase(username: String?): Boolean

    fun existsByEmailIgnoreCase(email: String?): Boolean

}
