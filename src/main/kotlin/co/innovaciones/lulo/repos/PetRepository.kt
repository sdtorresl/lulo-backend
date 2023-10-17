package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.Customer
import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.domain.Pet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface PetRepository : JpaRepository<Pet, Long> {

    fun findAllById(id: Long?, pageable: Pageable): Page<Pet>

    fun existsByProfilePictureId(id: Long?): Boolean

    fun findFirstByOwner(customer: Customer): Pet?

    fun findFirstByProfilePicture(`file`: File): Pet?

}
