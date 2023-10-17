package co.innovaciones.lulo.rest

import co.innovaciones.lulo.model.TransactionDTO
import co.innovaciones.lulo.service.TransactionService
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
    value = ["/api/transactions"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@SecurityRequirement(name = "bearer-jwt")
class TransactionResource(
    private val transactionService: TransactionService
) {

    @GetMapping
    fun getAllTransactions(@RequestParam(required = false, name = "filter") filter: String?):
            ResponseEntity<List<TransactionDTO>> =
            ResponseEntity.ok(transactionService.findAll(filter))

    @GetMapping("/{id}")
    fun getTransaction(@PathVariable(name = "id") id: Long): ResponseEntity<TransactionDTO> =
            ResponseEntity.ok(transactionService.get(id))

    @PostMapping
    @ApiResponse(responseCode = "201")
    fun createTransaction(@RequestBody @Valid transactionDTO: TransactionDTO):
            ResponseEntity<Long> {
        val createdId = transactionService.create(transactionDTO)
        return ResponseEntity(createdId, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateTransaction(@PathVariable(name = "id") id: Long, @RequestBody @Valid
            transactionDTO: TransactionDTO): ResponseEntity<Long> {
        transactionService.update(id, transactionDTO)
        return ResponseEntity.ok(id)
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    fun deleteTransaction(@PathVariable(name = "id") id: Long): ResponseEntity<Void> {
        transactionService.delete(id)
        return ResponseEntity.noContent().build()
    }

}
