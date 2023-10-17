package co.innovaciones.lulo.rest

import co.innovaciones.lulo.model.RegistrationRequest
import co.innovaciones.lulo.service.RegistrationService
import jakarta.validation.Valid
import java.lang.Void
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException


@RestController
class RegistrationResource(
    private val registrationService: RegistrationService
) {

    @PostMapping("/register")
    fun register(@RequestBody @Valid registrationRequest: RegistrationRequest):
            ResponseEntity<Void> {
        if (registrationService.usernameExists(registrationRequest)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "registration.register.taken")
        }
        registrationService.register(registrationRequest)
        return ResponseEntity.ok().build()
    }

}
