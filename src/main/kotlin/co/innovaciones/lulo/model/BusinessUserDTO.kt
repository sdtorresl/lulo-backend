package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull


class BusinessUserDTO {

    var id: Long? = null

    @NotNull
    var profilePicture: Long? = null

    @NotNull
    var brand: Long? = null

    @NotNull
    var user: Long? = null

}
