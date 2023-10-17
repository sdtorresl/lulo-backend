package co.innovaciones.lulo.rest

import co.innovaciones.lulo.model.BusinessUserDTO
import co.innovaciones.lulo.model.SimplePage
import co.innovaciones.lulo.service.BusinessUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import java.lang.Void
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
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
    value = ["/api/businessUsers"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@SecurityRequirement(name = "bearer-jwt")
class BusinessUserResource(
    private val businessUserService: BusinessUserService
) {

    @Operation(parameters = [
        Parameter(name = "page", `in` = ParameterIn.QUERY, schema = Schema(implementation =
                    Int::class)),
        Parameter(name = "size", `in` = ParameterIn.QUERY, schema = Schema(implementation =
                    Int::class)),
        Parameter(name = "sort", `in` = ParameterIn.QUERY, schema = Schema(implementation =
                    String::class))
    ])
    @GetMapping
    fun getAllBusinessUsers(@RequestParam(required = false, name = "filter") filter: String?,
            @Parameter(hidden = true) @SortDefault(sort = ["id"]) @PageableDefault(size = 20)
            pageable: Pageable): ResponseEntity<SimplePage<BusinessUserDTO>> =
            ResponseEntity.ok(businessUserService.findAll(filter, pageable))

    @GetMapping("/{id}")
    fun getBusinessUser(@PathVariable(name = "id") id: Long): ResponseEntity<BusinessUserDTO> =
            ResponseEntity.ok(businessUserService.get(id))

    @PostMapping
    @ApiResponse(responseCode = "201")
    fun createBusinessUser(@RequestBody @Valid businessUserDTO: BusinessUserDTO):
            ResponseEntity<Long> {
        val createdId = businessUserService.create(businessUserDTO)
        return ResponseEntity(createdId, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateBusinessUser(@PathVariable(name = "id") id: Long, @RequestBody @Valid
            businessUserDTO: BusinessUserDTO): ResponseEntity<Long> {
        businessUserService.update(id, businessUserDTO)
        return ResponseEntity.ok(id)
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    fun deleteBusinessUser(@PathVariable(name = "id") id: Long): ResponseEntity<Void> {
        businessUserService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
