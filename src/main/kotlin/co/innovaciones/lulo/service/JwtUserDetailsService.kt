package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.JwtUserDetails
import co.innovaciones.lulo.model.UsersRole
import co.innovaciones.lulo.repos.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class JwtUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): JwtUserDetails {
        val user = userRepository.findByUsernameIgnoreCase(username)
        if (user == null) {
            log.warn("user not found: {}", username)
            throw UsernameNotFoundException("User ${username} not found")
        }
        val authorities = listOf(SimpleGrantedAuthority(UsersRole.ADMIN.name))
        return JwtUserDetails(user.id, username, user.password, authorities)
    }


    companion object {

        val log: Logger = LoggerFactory.getLogger(JwtUserDetailsService::class.java)

    }

}
