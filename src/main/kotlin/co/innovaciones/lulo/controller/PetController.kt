package co.innovaciones.lulo.controller

import co.innovaciones.lulo.domain.Customer
import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.model.PetDTO
import co.innovaciones.lulo.model.PetType
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.service.PetService
import co.innovaciones.lulo.util.CustomCollectors
import co.innovaciones.lulo.util.WebUtils
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Controller
@RequestMapping("/pets")
class PetController(
    private val petService: PetService,
    private val customerRepository: CustomerRepository,
    private val fileRepository: FileRepository
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
        model.addAttribute("typeValues", PetType.values())
        model.addAttribute("ownerValues", customerRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Customer::id, Customer::id)))
        model.addAttribute("profilePictureValues", fileRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(File::id, File::name)))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) filter: String?,
        @SortDefault(sort = ["id"]) @PageableDefault(size = 20) pageable: Pageable,
        model: Model
    ): String {
        val pets = petService.findAll(filter, pageable)
        model.addAttribute("pets", pets)
        model.addAttribute("filter", filter)
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(pets))
        return "pet/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("pet") petDTO: PetDTO): String = "pet/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("pet") @Valid petDTO: PetDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!bindingResult.hasFieldErrors("profilePicture") && petDTO.profilePicture != null &&
                petService.profilePictureExists(petDTO.profilePicture)) {
            bindingResult.rejectValue("profilePicture", "Exists.pet.profilePicture")
        }
        if (bindingResult.hasErrors()) {
            return "pet/add"
        }
        petService.create(petDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("pet.create.success"))
        return "redirect:/pets"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("pet", petService.get(id))
        return "pet/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("pet") @Valid petDTO: PetDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentPetDTO: PetDTO = petService.get(id)
        if (!bindingResult.hasFieldErrors("profilePicture") && petDTO.profilePicture != null &&
                !petDTO.profilePicture!!.equals(currentPetDTO.profilePicture) &&
                petService.profilePictureExists(petDTO.profilePicture)) {
            bindingResult.rejectValue("profilePicture", "Exists.pet.profilePicture")
        }
        if (bindingResult.hasErrors()) {
            return "pet/edit"
        }
        petService.update(id, petDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("pet.update.success"))
        return "redirect:/pets"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        petService.delete(id)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                WebUtils.getMessage("pet.delete.success"))
        return "redirect:/pets"
    }

}
