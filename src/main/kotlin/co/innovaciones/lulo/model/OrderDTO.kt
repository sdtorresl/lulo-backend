package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


class OrderDTO {

    var id: Long? = null

    @NotNull
    @Size(max = 255)
    var sessionId: String? = null

    @NotNull
    var subtotal: Double? = null

    var itemDiscount: Double? = null

    @NotNull
    var discount: Double? = null

    @NotNull
    var tax: Double? = null

    @NotNull
    var shipping: Double? = null

    @NotNull
    var total: Double? = null

    var content: String? = null

    @NotNull
    var customer: Long? = null

    var deliveryAddress: Long? = null

}
