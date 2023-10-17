package co.innovaciones.lulo.config

import co.innovaciones.lulo.service.ActuatorUserDetailsService
import java.lang.Exception
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@Order(20)
class ActuatorSecurityConfig(
    private val actuatorUserDetailsService: ActuatorUserDetailsService,
    private val passwordEncoder: PasswordEncoder
) {

    @Bean
    fun actuatorAuthenticationManager(): AuthenticationManager {
        val actuatorAuthenticationManager = DaoAuthenticationProvider(passwordEncoder)
        actuatorAuthenticationManager.setUserDetailsService(actuatorUserDetailsService)
        return ProviderManager(actuatorAuthenticationManager)
    }

    @Bean
    @Throws(Exception::class)
    fun actuatorFilterChain(http: HttpSecurity): SecurityFilterChain = http.csrf { csrf ->
            csrf.disable() }
            .securityMatcher(EndpointRequest.toAnyEndpoint())
            .authenticationManager(actuatorAuthenticationManager())
            .authorizeHttpRequests { requests ->
            requests.anyRequest().hasAuthority(ActuatorUserDetailsService.ACTUATOR_ADMIN) }
            .httpBasic { basic -> basic.realmName("actuator realm") }
            .build()

}
