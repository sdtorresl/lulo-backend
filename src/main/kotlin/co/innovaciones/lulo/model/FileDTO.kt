package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size


class FileDTO {

    var id: Long? = null

    @Size(max = 255)
    var name: String? = null

    @NotNull
    @Size(max = 255)
    var url: String? = null

    @Size(max = 100)
    var type: String? = null

}
