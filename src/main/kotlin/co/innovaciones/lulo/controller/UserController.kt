package co.innovaciones.lulo.controller

import co.innovaciones.lulo.model.UserDTO
import co.innovaciones.lulo.model.UsersRole
import co.innovaciones.lulo.service.UserService
import co.innovaciones.lulo.util.WebUtils
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
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
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
        model.addAttribute("roleValues", UsersRole.values())
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) filter: String?,
        @SortDefault(sort = ["id"]) @PageableDefault(size = 20) pageable: Pageable,
        model: Model
    ): String {
        val users = userService.findAll(filter, pageable)
        model.addAttribute("users", users)
        model.addAttribute("filter", filter)
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(users))
        return "user/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("user") userDTO: UserDTO): String = "user/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("user") @Valid userDTO: UserDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!bindingResult.hasFieldErrors("username") &&
                userService.usernameExists(userDTO.username)) {
            bindingResult.rejectValue("username", "Exists.user.username")
        }
        if (!bindingResult.hasFieldErrors("email") && userService.emailExists(userDTO.email)) {
            bindingResult.rejectValue("email", "Exists.user.email")
        }
        if (bindingResult.hasErrors()) {
            return "user/add"
        }
        userService.create(userDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("user.create.success"))
        return "redirect:/users"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("user", userService.get(id))
        return "user/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("user") @Valid userDTO: UserDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentUserDTO: UserDTO = userService.get(id)
        if (!bindingResult.hasFieldErrors("username") &&
                !userDTO.username!!.equals(currentUserDTO.username, ignoreCase = true) &&
                userService.usernameExists(userDTO.username)) {
            bindingResult.rejectValue("username", "Exists.user.username")
        }
        if (!bindingResult.hasFieldErrors("email") &&
                !userDTO.email!!.equals(currentUserDTO.email, ignoreCase = true) &&
                userService.emailExists(userDTO.email)) {
            bindingResult.rejectValue("email", "Exists.user.email")
        }
        if (bindingResult.hasErrors()) {
            return "user/edit"
        }
        userService.update(id, userDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("user.update.success"))
        return "redirect:/users"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        val referencedWarning: String? = userService.getReferencedWarning(id)
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning)
        } else {
            userService.delete(id)
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                    WebUtils.getMessage("user.delete.success"))
        }
        return "redirect:/users"
    }

}
