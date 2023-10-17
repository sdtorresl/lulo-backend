package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.Pet
import co.innovaciones.lulo.model.PetDTO
import co.innovaciones.lulo.model.SimplePage
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.repos.PetRepository
import co.innovaciones.lulo.util.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class PetServiceImpl(
    private val petRepository: PetRepository,
    private val customerRepository: CustomerRepository,
    private val fileRepository: FileRepository
) : PetService {

    override fun findAll(filter: String?, pageable: Pageable): SimplePage<PetDTO> {
        var page: Page<Pet>
        if (filter != null) {
            page = petRepository.findAllById(filter.toLongOrNull(), pageable)
        } else {
            page = petRepository.findAll(pageable)
        }
        return SimplePage(page.content
                .stream()
                .map { pet -> mapToDTO(pet, PetDTO()) }
                .toList(),
                page.totalElements, pageable)
    }

    override fun `get`(id: Long): PetDTO = petRepository.findById(id)
            .map { pet -> mapToDTO(pet, PetDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(petDTO: PetDTO): Long {
        val pet = Pet()
        mapToEntity(petDTO, pet)
        return petRepository.save(pet).id!!
    }

    override fun update(id: Long, petDTO: PetDTO) {
        val pet = petRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(petDTO, pet)
        petRepository.save(pet)
    }

    override fun delete(id: Long) {
        petRepository.deleteById(id)
    }

    private fun mapToDTO(pet: Pet, petDTO: PetDTO): PetDTO {
        petDTO.id = pet.id
        petDTO.name = pet.name
        petDTO.birthdate = pet.birthdate
        petDTO.type = pet.type
        petDTO.breed = pet.breed
        petDTO.owner = pet.owner?.id
        petDTO.profilePicture = pet.profilePicture?.id
        return petDTO
    }

    private fun mapToEntity(petDTO: PetDTO, pet: Pet): Pet {
        pet.name = petDTO.name
        pet.birthdate = petDTO.birthdate
        pet.type = petDTO.type
        pet.breed = petDTO.breed
        val owner = if (petDTO.owner == null) null else customerRepository.findById(petDTO.owner!!)
                .orElseThrow { NotFoundException("owner not found") }
        pet.owner = owner
        val profilePicture = if (petDTO.profilePicture == null) null else
                fileRepository.findById(petDTO.profilePicture!!)
                .orElseThrow { NotFoundException("profilePicture not found") }
        pet.profilePicture = profilePicture
        return pet
    }

    override fun profilePictureExists(id: Long?): Boolean =
            petRepository.existsByProfilePictureId(id)

}
