package co.innovaciones.lulo.controller

import co.innovaciones.lulo.domain.Customer
import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.model.BusinessDTO
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.service.BusinessService
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
@RequestMapping("/businesss")
class BusinessController(
    private val businessService: BusinessService,
    private val fileRepository: FileRepository,
    private val customerRepository: CustomerRepository
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
        model.addAttribute("logoValues", fileRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(File::id, File::name)))
        model.addAttribute("customersValues", customerRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Customer::id, Customer::id)))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) filter: String?,
        @SortDefault(sort = ["id"]) @PageableDefault(size = 20) pageable: Pageable,
        model: Model
    ): String {
        val businesss = businessService.findAll(filter, pageable)
        model.addAttribute("businesss", businesss)
        model.addAttribute("filter", filter)
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(businesss))
        return "business/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("business") businessDTO: BusinessDTO): String = "business/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("business") @Valid businessDTO: BusinessDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!bindingResult.hasFieldErrors("logo") && businessDTO.logo != null &&
                businessService.logoExists(businessDTO.logo)) {
            bindingResult.rejectValue("logo", "Exists.business.logo")
        }
        if (bindingResult.hasErrors()) {
            return "business/add"
        }
        businessService.create(businessDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("business.create.success"))
        return "redirect:/businesss"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("business", businessService.get(id))
        return "business/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("business") @Valid businessDTO: BusinessDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentBusinessDTO: BusinessDTO = businessService.get(id)
        if (!bindingResult.hasFieldErrors("logo") && businessDTO.logo != null &&
                !businessDTO.logo!!.equals(currentBusinessDTO.logo) &&
                businessService.logoExists(businessDTO.logo)) {
            bindingResult.rejectValue("logo", "Exists.business.logo")
        }
        if (bindingResult.hasErrors()) {
            return "business/edit"
        }
        businessService.update(id, businessDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("business.update.success"))
        return "redirect:/businesss"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        val referencedWarning: String? = businessService.getReferencedWarning(id)
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning)
        } else {
            businessService.delete(id)
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                    WebUtils.getMessage("business.delete.success"))
        }
        return "redirect:/businesss"
    }

}
