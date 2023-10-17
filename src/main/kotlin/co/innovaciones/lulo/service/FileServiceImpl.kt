package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.model.FileDTO
import co.innovaciones.lulo.repos.BusinessRepository
import co.innovaciones.lulo.repos.BusinessUserRepository
import co.innovaciones.lulo.repos.CategoryRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.repos.PetRepository
import co.innovaciones.lulo.repos.ProductRepository
import co.innovaciones.lulo.util.NotFoundException
import co.innovaciones.lulo.util.WebUtils
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service


@Service
class FileServiceImpl(
    private val fileRepository: FileRepository,
    private val petRepository: PetRepository,
    private val businessRepository: BusinessRepository,
    private val businessUserRepository: BusinessUserRepository,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : FileService {

    override fun findAll(): List<FileDTO> {
        val files = fileRepository.findAll(Sort.by("id"))
        return files.stream()
                .map { file -> mapToDTO(file, FileDTO()) }
                .toList()
    }

    override fun `get`(id: Long): FileDTO = fileRepository.findById(id)
            .map { file -> mapToDTO(file, FileDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(fileDTO: FileDTO): Long {
        val file = File()
        mapToEntity(fileDTO, file)
        return fileRepository.save(file).id!!
    }

    override fun update(id: Long, fileDTO: FileDTO) {
        val file = fileRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(fileDTO, file)
        fileRepository.save(file)
    }

    override fun delete(id: Long) {
        fileRepository.deleteById(id)
    }

    private fun mapToDTO(`file`: File, fileDTO: FileDTO): FileDTO {
        fileDTO.id = file.id
        fileDTO.name = file.name
        fileDTO.url = file.url
        fileDTO.type = file.type
        return fileDTO
    }

    private fun mapToEntity(fileDTO: FileDTO, `file`: File): File {
        file.name = fileDTO.name
        file.url = fileDTO.url
        file.type = fileDTO.type
        return file
    }

    override fun getReferencedWarning(id: Long): String? {
        val file = fileRepository.findById(id)
                .orElseThrow { NotFoundException() }
        val profilePicturePet = petRepository.findFirstByProfilePicture(file)
        if (profilePicturePet != null) {
            return WebUtils.getMessage("file.pet.profilePicture.referenced", profilePicturePet.id)
        }
        val logoBusiness = businessRepository.findFirstByLogo(file)
        if (logoBusiness != null) {
            return WebUtils.getMessage("file.business.logo.referenced", logoBusiness.id)
        }
        val profilePictureBusinessUser = businessUserRepository.findFirstByProfilePicture(file)
        if (profilePictureBusinessUser != null) {
            return WebUtils.getMessage("file.businessUser.profilePicture.referenced",
                    profilePictureBusinessUser.id)
        }
        val pictureProduct = productRepository.findFirstByPicture(file)
        if (pictureProduct != null) {
            return WebUtils.getMessage("file.product.picture.referenced", pictureProduct.id)
        }
        val fileCategory = categoryRepository.findFirstByFile(file)
        if (fileCategory != null) {
            return WebUtils.getMessage("file.category.file.referenced", fileCategory.id)
        }
        return null
    }

}
