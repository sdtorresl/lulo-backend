package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.BusinessUser
import co.innovaciones.lulo.model.BusinessUserDTO
import co.innovaciones.lulo.model.SimplePage
import co.innovaciones.lulo.repos.BusinessRepository
import co.innovaciones.lulo.repos.BusinessUserRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.repos.UserRepository
import co.innovaciones.lulo.util.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class BusinessUserServiceImpl(
    private val businessUserRepository: BusinessUserRepository,
    private val fileRepository: FileRepository,
    private val businessRepository: BusinessRepository,
    private val userRepository: UserRepository
) : BusinessUserService {

    override fun findAll(filter: String?, pageable: Pageable): SimplePage<BusinessUserDTO> {
        var page: Page<BusinessUser>
        if (filter != null) {
            page = businessUserRepository.findAllById(filter.toLongOrNull(), pageable)
        } else {
            page = businessUserRepository.findAll(pageable)
        }
        return SimplePage(page.content
                .stream()
                .map { businessUser -> mapToDTO(businessUser, BusinessUserDTO()) }
                .toList(),
                page.totalElements, pageable)
    }

    override fun `get`(id: Long): BusinessUserDTO = businessUserRepository.findById(id)
            .map { businessUser -> mapToDTO(businessUser, BusinessUserDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(businessUserDTO: BusinessUserDTO): Long {
        val businessUser = BusinessUser()
        mapToEntity(businessUserDTO, businessUser)
        return businessUserRepository.save(businessUser).id!!
    }

    override fun update(id: Long, businessUserDTO: BusinessUserDTO) {
        val businessUser = businessUserRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(businessUserDTO, businessUser)
        businessUserRepository.save(businessUser)
    }

    override fun delete(id: Long) {
        businessUserRepository.deleteById(id)
    }

    private fun mapToDTO(businessUser: BusinessUser, businessUserDTO: BusinessUserDTO):
            BusinessUserDTO {
        businessUserDTO.id = businessUser.id
        businessUserDTO.profilePicture = businessUser.profilePicture?.id
        businessUserDTO.brand = businessUser.brand?.id
        businessUserDTO.user = businessUser.user?.id
        return businessUserDTO
    }

    private fun mapToEntity(businessUserDTO: BusinessUserDTO, businessUser: BusinessUser):
            BusinessUser {
        val profilePicture = if (businessUserDTO.profilePicture == null) null else
                fileRepository.findById(businessUserDTO.profilePicture!!)
                .orElseThrow { NotFoundException("profilePicture not found") }
        businessUser.profilePicture = profilePicture
        val brand = if (businessUserDTO.brand == null) null else
                businessRepository.findById(businessUserDTO.brand!!)
                .orElseThrow { NotFoundException("brand not found") }
        businessUser.brand = brand
        val user = if (businessUserDTO.user == null) null else
                userRepository.findById(businessUserDTO.user!!)
                .orElseThrow { NotFoundException("user not found") }
        businessUser.user = user
        return businessUser
    }

    override fun profilePictureExists(id: Long?): Boolean =
            businessUserRepository.existsByProfilePictureId(id)

    override fun userExists(id: Long?): Boolean = businessUserRepository.existsByUserId(id)

}
