package co.innovaciones.lulo.controller

import co.innovaciones.lulo.domain.Address
import co.innovaciones.lulo.domain.Customer
import co.innovaciones.lulo.model.OrderDTO
import co.innovaciones.lulo.repos.AddressRepository
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.service.OrderService
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Controller
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
    private val customerRepository: CustomerRepository,
    private val addressRepository: AddressRepository
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
        model.addAttribute("customerValues", customerRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Customer::id, Customer::id)))
        model.addAttribute("deliveryAddressValues", addressRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Address::id, Address::address)))
    }

    @GetMapping
    fun list(@RequestParam(required = false) filter: String?, model: Model): String {
        model.addAttribute("orders", orderService.findAll(filter))
        model.addAttribute("filter", filter)
        return "order/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("order") orderDTO: OrderDTO): String = "order/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("order") @Valid orderDTO: OrderDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!bindingResult.hasFieldErrors("sessionId") &&
                orderService.sessionIdExists(orderDTO.sessionId)) {
            bindingResult.rejectValue("sessionId", "Exists.order.sessionId")
        }
        if (!bindingResult.hasFieldErrors("deliveryAddress") && orderDTO.deliveryAddress != null &&
                orderService.deliveryAddressExists(orderDTO.deliveryAddress)) {
            bindingResult.rejectValue("deliveryAddress", "Exists.order.deliveryAddress")
        }
        if (bindingResult.hasErrors()) {
            return "order/add"
        }
        orderService.create(orderDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("order.create.success"))
        return "redirect:/orders"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("order", orderService.get(id))
        return "order/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("order") @Valid orderDTO: OrderDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentOrderDTO: OrderDTO = orderService.get(id)
        if (!bindingResult.hasFieldErrors("sessionId") &&
                !orderDTO.sessionId!!.equals(currentOrderDTO.sessionId, ignoreCase = true) &&
                orderService.sessionIdExists(orderDTO.sessionId)) {
            bindingResult.rejectValue("sessionId", "Exists.order.sessionId")
        }
        if (!bindingResult.hasFieldErrors("deliveryAddress") && orderDTO.deliveryAddress != null &&
                !orderDTO.deliveryAddress!!.equals(currentOrderDTO.deliveryAddress) &&
                orderService.deliveryAddressExists(orderDTO.deliveryAddress)) {
            bindingResult.rejectValue("deliveryAddress", "Exists.order.deliveryAddress")
        }
        if (bindingResult.hasErrors()) {
            return "order/edit"
        }
        orderService.update(id, orderDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("order.update.success"))
        return "redirect:/orders"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        val referencedWarning: String? = orderService.getReferencedWarning(id)
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning)
        } else {
            orderService.delete(id)
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                    WebUtils.getMessage("order.delete.success"))
        }
        return "redirect:/orders"
    }

}
