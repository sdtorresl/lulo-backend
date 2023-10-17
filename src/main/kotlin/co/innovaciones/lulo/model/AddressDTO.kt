package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


class AddressDTO {

    var id: Long? = null

    @NotNull
    @Size(max = 255)
    var address: String? = null

    @Size(max = 255)
    var line1: String? = null

    @Size(max = 255)
    var line2: String? = null

    @Size(max = 255)
    var city: String? = null

    @Size(max = 255)
    var province: String? = null

    @Size(max = 255)
    var country: String? = null

    var postalCode: Int? = null

    var customer: Long? = null

}
