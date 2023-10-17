package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime


class RegistrationRequest {

    @NotNull
    @Size(max = 50)
    var firstName: String? = null

    @Size(max = 50)
    var lastName: String? = null

    @NotNull
    @Size(max = 50)
    var username: String? = null

    @NotNull
    @Size(max = 100)
    var email: String? = null

    @Size(max = 50)
    var phone: String? = null

    @NotNull
    @Size(max = 255)
    var password: String? = null

    var lastLogin: OffsetDateTime? = null

    @NotNull
    var enabled: Boolean? = null

    @NotNull
    var role: UsersRole? = null

}
