package co.innovaciones.lulo.controller

import co.innovaciones.lulo.domain.Business
import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.domain.User
import co.innovaciones.lulo.model.BusinessUserDTO
import co.innovaciones.lulo.repos.BusinessRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.repos.UserRepository
import co.innovaciones.lulo.service.BusinessUserService
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
@RequestMapping("/businessUsers")
class BusinessUserController(
    private val businessUserService: BusinessUserService,
    private val fileRepository: FileRepository,
    private val businessRepository: BusinessRepository,
    private val userRepository: UserRepository
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
        model.addAttribute("profilePictureValues", fileRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(File::id, File::name)))
        model.addAttribute("brandValues", businessRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Business::id, Business::name)))
        model.addAttribute("userValues", userRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::id, User::firstName)))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) filter: String?,
        @SortDefault(sort = ["id"]) @PageableDefault(size = 20) pageable: Pageable,
        model: Model
    ): String {
        val businessUsers = businessUserService.findAll(filter, pageable)
        model.addAttribute("businessUsers", businessUsers)
        model.addAttribute("filter", filter)
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(businessUsers))
        return "businessUser/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("businessUser") businessUserDTO: BusinessUserDTO): String =
            "businessUser/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("businessUser") @Valid businessUserDTO: BusinessUserDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!bindingResult.hasFieldErrors("profilePicture") && businessUserDTO.profilePicture !=
                null && businessUserService.profilePictureExists(businessUserDTO.profilePicture)) {
            bindingResult.rejectValue("profilePicture", "Exists.businessUser.profilePicture")
        }
        if (!bindingResult.hasFieldErrors("user") && businessUserDTO.user != null &&
                businessUserService.userExists(businessUserDTO.user)) {
            bindingResult.rejectValue("user", "Exists.businessUser.user")
        }
        if (bindingResult.hasErrors()) {
            return "businessUser/add"
        }
        businessUserService.create(businessUserDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("businessUser.create.success"))
        return "redirect:/businessUsers"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("businessUser", businessUserService.get(id))
        return "businessUser/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("businessUser") @Valid businessUserDTO: BusinessUserDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentBusinessUserDTO: BusinessUserDTO = businessUserService.get(id)
        if (!bindingResult.hasFieldErrors("profilePicture") && businessUserDTO.profilePicture !=
                null &&
                !businessUserDTO.profilePicture!!.equals(currentBusinessUserDTO.profilePicture) &&
                businessUserService.profilePictureExists(businessUserDTO.profilePicture)) {
            bindingResult.rejectValue("profilePicture", "Exists.businessUser.profilePicture")
        }
        if (!bindingResult.hasFieldErrors("user") && businessUserDTO.user != null &&
                !businessUserDTO.user!!.equals(currentBusinessUserDTO.user) &&
                businessUserService.userExists(businessUserDTO.user)) {
            bindingResult.rejectValue("user", "Exists.businessUser.user")
        }
        if (bindingResult.hasErrors()) {
            return "businessUser/edit"
        }
        businessUserService.update(id, businessUserDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("businessUser.update.success"))
        return "redirect:/businessUsers"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        businessUserService.delete(id)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                WebUtils.getMessage("businessUser.delete.success"))
        return "redirect:/businessUsers"
    }

}
