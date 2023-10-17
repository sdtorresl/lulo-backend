package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


class TransactionDTO {

    var id: Long? = null

    @NotNull
    @Size(max = 255)
    var session: String? = null

    var content: String? = null

    @NotNull
    var order: Long? = null

}
