package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.User
import co.innovaciones.lulo.model.RegistrationRequest
import co.innovaciones.lulo.repos.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class RegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun usernameExists(registrationRequest: RegistrationRequest): Boolean =
            userRepository.existsByUsernameIgnoreCase(registrationRequest.username)

    fun register(registrationRequest: RegistrationRequest) {
        log.info("registering new user: {}", registrationRequest.username)

        val user = User()
        user.firstName = registrationRequest.firstName
        user.lastName = registrationRequest.lastName
        user.username = registrationRequest.username
        user.email = registrationRequest.email
        user.phone = registrationRequest.phone
        user.password = passwordEncoder.encode(registrationRequest.password)
        user.lastLogin = registrationRequest.lastLogin
        user.enabled = registrationRequest.enabled
        user.role = registrationRequest.role
        userRepository.save(user)
    }


    companion object {

        val log: Logger = LoggerFactory.getLogger(RegistrationService::class.java)

    }

}
