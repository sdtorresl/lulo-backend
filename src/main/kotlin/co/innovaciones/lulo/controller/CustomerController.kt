package co.innovaciones.lulo.controller

import co.innovaciones.lulo.domain.User
import co.innovaciones.lulo.model.CustomerDTO
import co.innovaciones.lulo.repos.UserRepository
import co.innovaciones.lulo.service.CustomerService
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
@RequestMapping("/customers")
class CustomerController(
    private val customerService: CustomerService,
    private val userRepository: UserRepository
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
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
        val customers = customerService.findAll(filter, pageable)
        model.addAttribute("customers", customers)
        model.addAttribute("filter", filter)
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(customers))
        return "customer/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("customer") customerDTO: CustomerDTO): String = "customer/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("customer") @Valid customerDTO: CustomerDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!bindingResult.hasFieldErrors("user") && customerDTO.user != null &&
                customerService.userExists(customerDTO.user)) {
            bindingResult.rejectValue("user", "Exists.customer.user")
        }
        if (bindingResult.hasErrors()) {
            return "customer/add"
        }
        customerService.create(customerDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("customer.create.success"))
        return "redirect:/customers"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("customer", customerService.get(id))
        return "customer/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("customer") @Valid customerDTO: CustomerDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentCustomerDTO: CustomerDTO = customerService.get(id)
        if (!bindingResult.hasFieldErrors("user") && customerDTO.user != null &&
                !customerDTO.user!!.equals(currentCustomerDTO.user) &&
                customerService.userExists(customerDTO.user)) {
            bindingResult.rejectValue("user", "Exists.customer.user")
        }
        if (bindingResult.hasErrors()) {
            return "customer/edit"
        }
        customerService.update(id, customerDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("customer.update.success"))
        return "redirect:/customers"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        val referencedWarning: String? = customerService.getReferencedWarning(id)
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning)
        } else {
            customerService.delete(id)
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                    WebUtils.getMessage("customer.delete.success"))
        }
        return "redirect:/customers"
    }

}
