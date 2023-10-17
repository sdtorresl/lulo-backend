package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


class CategoryDTO {

    var id: Long? = null

    @NotNull
    @Size(max = 255)
    var name: String? = null

    var type: CategoryType? = null

    var products: List<Long>? = null

    @NotNull
    var `file`: Long? = null

    var business: Long? = null

}
