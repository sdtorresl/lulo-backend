package co.innovaciones.lulo.rest

import co.innovaciones.lulo.model.AuthenticationRequest
import co.innovaciones.lulo.model.AuthenticationResponse
import co.innovaciones.lulo.service.JwtTokenService
import co.innovaciones.lulo.service.JwtUserDetailsService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException


@RestController
class AuthenticationResource(
    @Qualifier("jwtAuthenticationManager")
    private val jwtAuthenticationManager: AuthenticationManager,
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val jwtTokenService: JwtTokenService
) {

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody @Valid authenticationRequest: AuthenticationRequest):
            AuthenticationResponse {
        try {
            jwtAuthenticationManager.authenticate(UsernamePasswordAuthenticationToken(authenticationRequest.username,
                    authenticationRequest.password))
        } catch (ex: BadCredentialsException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.username!!)
        val authenticationResponse = AuthenticationResponse()
        authenticationResponse.accessToken = jwtTokenService.generateToken(userDetails)
        return authenticationResponse
    }

}
