package co.innovaciones.lulo.rest

import co.innovaciones.lulo.model.AddressDTO
import co.innovaciones.lulo.service.AddressService
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
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(
    value = ["/api/addresss"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@SecurityRequirement(name = "bearer-jwt")
class AddressResource(
    private val addressService: AddressService
) {

    @GetMapping
    fun getAllAddresss(): ResponseEntity<List<AddressDTO>> =
            ResponseEntity.ok(addressService.findAll())

    @GetMapping("/{id}")
    fun getAddress(@PathVariable(name = "id") id: Long): ResponseEntity<AddressDTO> =
            ResponseEntity.ok(addressService.get(id))

    @PostMapping
    @ApiResponse(responseCode = "201")
    fun createAddress(@RequestBody @Valid addressDTO: AddressDTO): ResponseEntity<Long> {
        val createdId = addressService.create(addressDTO)
        return ResponseEntity(createdId, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateAddress(@PathVariable(name = "id") id: Long, @RequestBody @Valid
            addressDTO: AddressDTO): ResponseEntity<Long> {
        addressService.update(id, addressDTO)
        return ResponseEntity.ok(id)
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    fun deleteAddress(@PathVariable(name = "id") id: Long): ResponseEntity<Void> {
        addressService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
