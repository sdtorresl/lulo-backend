package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.User
import co.innovaciones.lulo.model.SimplePage
import co.innovaciones.lulo.model.UserDTO
import co.innovaciones.lulo.repos.BusinessUserRepository
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.repos.UserRepository
import co.innovaciones.lulo.util.NotFoundException
import co.innovaciones.lulo.util.WebUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val customerRepository: CustomerRepository,
    private val businessUserRepository: BusinessUserRepository
) : UserService {

    override fun findAll(filter: String?, pageable: Pageable): SimplePage<UserDTO> {
        var page: Page<User>
        if (filter != null) {
            page = userRepository.findAllById(filter.toLongOrNull(), pageable)
        } else {
            page = userRepository.findAll(pageable)
        }
        return SimplePage(page.content
                .stream()
                .map { user -> mapToDTO(user, UserDTO()) }
                .toList(),
                page.totalElements, pageable)
    }

    override fun `get`(id: Long): UserDTO = userRepository.findById(id)
            .map { user -> mapToDTO(user, UserDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(userDTO: UserDTO): Long {
        val user = User()
        mapToEntity(userDTO, user)
        return userRepository.save(user).id!!
    }

    override fun update(id: Long, userDTO: UserDTO) {
        val user = userRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(userDTO, user)
        userRepository.save(user)
    }

    override fun delete(id: Long) {
        userRepository.deleteById(id)
    }

    private fun mapToDTO(user: User, userDTO: UserDTO): UserDTO {
        userDTO.id = user.id
        userDTO.firstName = user.firstName
        userDTO.lastName = user.lastName
        userDTO.username = user.username
        userDTO.email = user.email
        userDTO.phone = user.phone
        userDTO.lastLogin = user.lastLogin
        userDTO.enabled = user.enabled
        userDTO.role = user.role
        return userDTO
    }

    private fun mapToEntity(userDTO: UserDTO, user: User): User {
        user.firstName = userDTO.firstName
        user.lastName = userDTO.lastName
        user.username = userDTO.username
        user.email = userDTO.email
        user.phone = userDTO.phone
        user.password = passwordEncoder.encode(userDTO.password)
        user.lastLogin = userDTO.lastLogin
        user.enabled = userDTO.enabled
        user.role = userDTO.role
        return user
    }

    override fun usernameExists(username: String?): Boolean =
            userRepository.existsByUsernameIgnoreCase(username)

    override fun emailExists(email: String?): Boolean =
            userRepository.existsByEmailIgnoreCase(email)

    override fun getReferencedWarning(id: Long): String? {
        val user = userRepository.findById(id)
                .orElseThrow { NotFoundException() }
        val userCustomer = customerRepository.findFirstByUser(user)
        if (userCustomer != null) {
            return WebUtils.getMessage("user.customer.user.referenced", userCustomer.id)
        }
        val userBusinessUser = businessUserRepository.findFirstByUser(user)
        if (userBusinessUser != null) {
            return WebUtils.getMessage("user.businessUser.user.referenced", userBusinessUser.id)
        }
        return null
    }

}
