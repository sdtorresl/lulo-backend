package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.Address
import co.innovaciones.lulo.domain.Customer
import org.springframework.data.jpa.repository.JpaRepository


interface AddressRepository : JpaRepository<Address, Long> {

    fun findFirstByCustomer(customer: Customer): Address?

}
