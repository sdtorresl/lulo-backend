package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


class BusinessDTO {

    var id: Long? = null

    @NotNull
    @Size(max = 255)
    var name: String? = null

    var description: String? = null

    @NotNull
    var logo: Long? = null

    var customers: List<Long>? = null

}
