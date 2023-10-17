package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull


class CustomerDTO {

    var id: Long? = null

    @NotNull
    var user: Long? = null

}
