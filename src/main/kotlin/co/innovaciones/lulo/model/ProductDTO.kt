package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


class ProductDTO {

    var id: Long? = null

    @NotNull
    @Size(max = 255)
    var name: String? = null

    @NotNull
    @Size(max = 255)
    var description: String? = null

    @NotNull
    var price: Double? = null

    @NotNull
    var quantity: Int? = null

    @NotNull
    var available: Boolean? = null

    @NotNull
    var picture: Long? = null

}
