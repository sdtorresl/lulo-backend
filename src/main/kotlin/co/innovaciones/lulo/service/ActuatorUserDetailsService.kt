package co.innovaciones.lulo.service

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class ActuatorUserDetailsService : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        if ("actuator" == username) {
            return User.withUsername(username)
                    .password("{bcrypt}\$2a\$12\$yHivLG1T/cQbdiOa2nG5K.Zz.KmobXV3UpGffvkh5myFewYxna1Ma")
                    .authorities(ACTUATOR_ADMIN)
                    .build()
        }
        throw UsernameNotFoundException("User ${username} not found")
    }


    companion object {

        const val ACTUATOR_ADMIN = "ACTUATOR_ADMIN"

    }

}
