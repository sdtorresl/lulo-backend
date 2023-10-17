package co.innovaciones.lulo.controller

import co.innovaciones.lulo.domain.Order
import co.innovaciones.lulo.model.TransactionDTO
import co.innovaciones.lulo.repos.OrderRepository
import co.innovaciones.lulo.service.TransactionService
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
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    private val orderRepository: OrderRepository
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
        model.addAttribute("orderValues", orderRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Order::id, Order::sessionId)))
    }

    @GetMapping
    fun list(@RequestParam(required = false) filter: String?, model: Model): String {
        model.addAttribute("transactions", transactionService.findAll(filter))
        model.addAttribute("filter", filter)
        return "transaction/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("transaction") transactionDTO: TransactionDTO): String =
            "transaction/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("transaction") @Valid transactionDTO: TransactionDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!bindingResult.hasFieldErrors("session") &&
                transactionService.sessionExists(transactionDTO.session)) {
            bindingResult.rejectValue("session", "Exists.transaction.session")
        }
        if (bindingResult.hasErrors()) {
            return "transaction/add"
        }
        transactionService.create(transactionDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("transaction.create.success"))
        return "redirect:/transactions"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("transaction", transactionService.get(id))
        return "transaction/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("transaction") @Valid transactionDTO: TransactionDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentTransactionDTO: TransactionDTO = transactionService.get(id)
        if (!bindingResult.hasFieldErrors("session") &&
                !transactionDTO.session!!.equals(currentTransactionDTO.session, ignoreCase = true)
                        &&
                transactionService.sessionExists(transactionDTO.session)) {
            bindingResult.rejectValue("session", "Exists.transaction.session")
        }
        if (bindingResult.hasErrors()) {
            return "transaction/edit"
        }
        transactionService.update(id, transactionDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("transaction.update.success"))
        return "redirect:/transactions"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        transactionService.delete(id)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                WebUtils.getMessage("transaction.delete.success"))
        return "redirect:/transactions"
    }

}
