package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.Customer
import co.innovaciones.lulo.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface CustomerRepository : JpaRepository<Customer, Long> {

    fun findAllById(id: Long?, pageable: Pageable): Page<Customer>

    fun existsByUserId(id: Long?): Boolean

    fun findFirstByUser(user: User): Customer?

}
