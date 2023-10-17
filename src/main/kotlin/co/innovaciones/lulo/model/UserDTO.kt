package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import org.springframework.format.annotation.DateTimeFormat


class UserDTO {

    var id: Long? = null

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

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    var lastLogin: OffsetDateTime? = null

    @NotNull
    var enabled: Boolean? = null

    @NotNull
    var role: UsersRole? = null

}
