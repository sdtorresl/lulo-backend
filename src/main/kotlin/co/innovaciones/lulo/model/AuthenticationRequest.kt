package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


class AuthenticationRequest {

    @NotNull
    @Size(max = 50)
    var username: String? = null

    @NotNull
    @Size(max = 255)
    var password: String? = null

}
