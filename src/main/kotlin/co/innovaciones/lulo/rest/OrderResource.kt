package co.innovaciones.lulo.rest

import co.innovaciones.lulo.model.OrderDTO
import co.innovaciones.lulo.service.OrderService
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import java.lang.Void
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(
    value = ["/api/orders"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@SecurityRequirement(name = "bearer-jwt")
class OrderResource(
    private val orderService: OrderService
) {

    @GetMapping
    fun getAllOrders(@RequestParam(required = false, name = "filter") filter: String?):
            ResponseEntity<List<OrderDTO>> = ResponseEntity.ok(orderService.findAll(filter))

    @GetMapping("/{id}")
    fun getOrder(@PathVariable(name = "id") id: Long): ResponseEntity<OrderDTO> =
            ResponseEntity.ok(orderService.get(id))

    @PostMapping
    @ApiResponse(responseCode = "201")
    fun createOrder(@RequestBody @Valid orderDTO: OrderDTO): ResponseEntity<Long> {
        val createdId = orderService.create(orderDTO)
        return ResponseEntity(createdId, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateOrder(@PathVariable(name = "id") id: Long, @RequestBody @Valid orderDTO: OrderDTO):
            ResponseEntity<Long> {
        orderService.update(id, orderDTO)
        return ResponseEntity.ok(id)
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    fun deleteOrder(@PathVariable(name = "id") id: Long): ResponseEntity<Void> {
        orderService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
