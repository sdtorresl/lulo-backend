package co.innovaciones.lulo.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate


class PetDTO {

    var id: Long? = null

    @NotNull
    @Size(max = 100)
    var name: String? = null

    @NotNull
    var birthdate: LocalDate? = null

    @NotNull
    var type: PetType? = null

    @NotNull
    @Size(max = 50)
    var breed: String? = null

    @NotNull
    var owner: Long? = null

    var profilePicture: Long? = null

}
