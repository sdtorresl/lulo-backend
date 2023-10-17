package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.Order
import co.innovaciones.lulo.model.OrderDTO
import co.innovaciones.lulo.repos.AddressRepository
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.repos.OrderItemRepository
import co.innovaciones.lulo.repos.OrderRepository
import co.innovaciones.lulo.repos.TransactionRepository
import co.innovaciones.lulo.util.NotFoundException
import co.innovaciones.lulo.util.WebUtils
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service


@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val addressRepository: AddressRepository,
    private val orderItemRepository: OrderItemRepository,
    private val transactionRepository: TransactionRepository
) : OrderService {

    override fun findAll(filter: String?): List<OrderDTO> {
        var orders: List<Order>
        val sort = Sort.by("id")
        if (filter != null) {
            orders = orderRepository.findAllById(filter.toLongOrNull(), sort)
        } else {
            orders = orderRepository.findAll(sort)
        }
        return orders.stream()
                .map { order -> mapToDTO(order, OrderDTO()) }
                .toList()
    }

    override fun `get`(id: Long): OrderDTO = orderRepository.findById(id)
            .map { order -> mapToDTO(order, OrderDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(orderDTO: OrderDTO): Long {
        val order = Order()
        mapToEntity(orderDTO, order)
        return orderRepository.save(order).id!!
    }

    override fun update(id: Long, orderDTO: OrderDTO) {
        val order = orderRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(orderDTO, order)
        orderRepository.save(order)
    }

    override fun delete(id: Long) {
        orderRepository.deleteById(id)
    }

    private fun mapToDTO(order: Order, orderDTO: OrderDTO): OrderDTO {
        orderDTO.id = order.id
        orderDTO.sessionId = order.sessionId
        orderDTO.subtotal = order.subtotal
        orderDTO.itemDiscount = order.itemDiscount
        orderDTO.discount = order.discount
        orderDTO.tax = order.tax
        orderDTO.shipping = order.shipping
        orderDTO.total = order.total
        orderDTO.content = order.content
        orderDTO.customer = order.customer?.id
        orderDTO.deliveryAddress = order.deliveryAddress?.id
        return orderDTO
    }

    private fun mapToEntity(orderDTO: OrderDTO, order: Order): Order {
        order.sessionId = orderDTO.sessionId
        order.subtotal = orderDTO.subtotal
        order.itemDiscount = orderDTO.itemDiscount
        order.discount = orderDTO.discount
        order.tax = orderDTO.tax
        order.shipping = orderDTO.shipping
        order.total = orderDTO.total
        order.content = orderDTO.content
        val customer = if (orderDTO.customer == null) null else
                customerRepository.findById(orderDTO.customer!!)
                .orElseThrow { NotFoundException("customer not found") }
        order.customer = customer
        val deliveryAddress = if (orderDTO.deliveryAddress == null) null else
                addressRepository.findById(orderDTO.deliveryAddress!!)
                .orElseThrow { NotFoundException("deliveryAddress not found") }
        order.deliveryAddress = deliveryAddress
        return order
    }

    override fun sessionIdExists(sessionId: String?): Boolean =
            orderRepository.existsBySessionIdIgnoreCase(sessionId)

    override fun deliveryAddressExists(id: Long?): Boolean =
            orderRepository.existsByDeliveryAddressId(id)

    override fun getReferencedWarning(id: Long): String? {
        val order = orderRepository.findById(id)
                .orElseThrow { NotFoundException() }
        val orderOrderItem = orderItemRepository.findFirstByOrder(order)
        if (orderOrderItem != null) {
            return WebUtils.getMessage("order.orderItem.order.referenced", orderOrderItem.id)
        }
        val orderTransaction = transactionRepository.findFirstByOrder(order)
        if (orderTransaction != null) {
            return WebUtils.getMessage("order.transaction.order.referenced", orderTransaction.id)
        }
        return null
    }

}
