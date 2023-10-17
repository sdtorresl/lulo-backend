package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.Address
import co.innovaciones.lulo.domain.Customer
import co.innovaciones.lulo.domain.Order
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository


interface OrderRepository : JpaRepository<Order, Long> {

    fun findAllById(id: Long?, sort: Sort): List<Order>

    fun findFirstByCustomer(customer: Customer): Order?

    fun existsBySessionIdIgnoreCase(sessionId: String?): Boolean

    fun existsByDeliveryAddressId(id: Long?): Boolean

    fun findFirstByDeliveryAddress(address: Address): Order?

}
