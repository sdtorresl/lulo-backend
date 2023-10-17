package co.innovaciones.lulo.controller

import co.innovaciones.lulo.domain.Customer
import co.innovaciones.lulo.model.AddressDTO
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.service.AddressService
import co.innovaciones.lulo.util.CustomCollectors
import co.innovaciones.lulo.util.WebUtils
import jakarta.validation.Valid
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Controller
@RequestMapping("/addresss")
class AddressController(
    private val addressService: AddressService,
    private val customerRepository: CustomerRepository
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
        model.addAttribute("customerValues", customerRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Customer::id, Customer::id)))
    }

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("addresss", addressService.findAll())
        return "address/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("address") addressDTO: AddressDTO): String = "address/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("address") @Valid addressDTO: AddressDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "address/add"
        }
        addressService.create(addressDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("address.create.success"))
        return "redirect:/addresss"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("address", addressService.get(id))
        return "address/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("address") @Valid addressDTO: AddressDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "address/edit"
        }
        addressService.update(id, addressDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("address.update.success"))
        return "redirect:/addresss"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        val referencedWarning: String? = addressService.getReferencedWarning(id)
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning)
        } else {
            addressService.delete(id)
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                    WebUtils.getMessage("address.delete.success"))
        }
        return "redirect:/addresss"
    }

}
