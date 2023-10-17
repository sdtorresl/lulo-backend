package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.Business
import co.innovaciones.lulo.domain.Customer
import co.innovaciones.lulo.domain.File
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface BusinessRepository : JpaRepository<Business, Long> {

    fun findAllById(id: Long?, pageable: Pageable): Page<Business>

    fun findAllByCustomers(customer: Customer): List<Business>

    fun findFirstByCustomers(customer: Customer): Business?

    fun existsByLogoId(id: Long?): Boolean

    fun findFirstByLogo(`file`: File): Business?

}
