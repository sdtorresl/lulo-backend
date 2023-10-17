package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.Business
import co.innovaciones.lulo.domain.BusinessUser
import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository


interface BusinessUserRepository : JpaRepository<BusinessUser, Long> {

    fun findAllById(id: Long?, pageable: Pageable): Page<BusinessUser>

    fun findFirstByBrand(business: Business): BusinessUser?

    fun existsByProfilePictureId(id: Long?): Boolean

    fun existsByUserId(id: Long?): Boolean

    fun findFirstByProfilePicture(`file`: File): BusinessUser?

    fun findFirstByUser(user: User): BusinessUser?

}
