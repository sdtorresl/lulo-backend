package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.Order
import co.innovaciones.lulo.domain.OrderItem
import co.innovaciones.lulo.domain.Product
import org.springframework.data.jpa.repository.JpaRepository


interface OrderItemRepository : JpaRepository<OrderItem, Long> {

    fun findFirstByProduct(product: Product): OrderItem?

    fun findFirstByOrder(order: Order): OrderItem?

}
