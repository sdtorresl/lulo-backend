package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.OrderDTO


interface OrderService {

    fun findAll(filter: String?): List<OrderDTO>

    fun `get`(id: Long): OrderDTO

    fun create(orderDTO: OrderDTO): Long

    fun update(id: Long, orderDTO: OrderDTO)

    fun delete(id: Long)

    fun sessionIdExists(sessionId: String?): Boolean

    fun deliveryAddressExists(id: Long?): Boolean

    fun getReferencedWarning(id: Long): String?

}
