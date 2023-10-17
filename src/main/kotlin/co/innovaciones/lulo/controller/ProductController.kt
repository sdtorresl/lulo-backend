package co.innovaciones.lulo.controller

import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.model.ProductDTO
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.service.ProductService
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
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val fileRepository: FileRepository
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
        model.addAttribute("pictureValues", fileRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(File::id, File::name)))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) filter: String?,
        @SortDefault(sort = ["id"]) @PageableDefault(size = 20) pageable: Pageable,
        model: Model
    ): String {
        val products = productService.findAll(filter, pageable)
        model.addAttribute("products", products)
        model.addAttribute("filter", filter)
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(products))
        return "product/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("product") productDTO: ProductDTO): String = "product/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("product") @Valid productDTO: ProductDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!bindingResult.hasFieldErrors("picture") && productDTO.picture != null &&
                productService.pictureExists(productDTO.picture)) {
            bindingResult.rejectValue("picture", "Exists.product.picture")
        }
        if (bindingResult.hasErrors()) {
            return "product/add"
        }
        productService.create(productDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("product.create.success"))
        return "redirect:/products"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("product", productService.get(id))
        return "product/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("product") @Valid productDTO: ProductDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentProductDTO: ProductDTO = productService.get(id)
        if (!bindingResult.hasFieldErrors("picture") && productDTO.picture != null &&
                !productDTO.picture!!.equals(currentProductDTO.picture) &&
                productService.pictureExists(productDTO.picture)) {
            bindingResult.rejectValue("picture", "Exists.product.picture")
        }
        if (bindingResult.hasErrors()) {
            return "product/edit"
        }
        productService.update(id, productDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("product.update.success"))
        return "redirect:/products"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        val referencedWarning: String? = productService.getReferencedWarning(id)
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning)
        } else {
            productService.delete(id)
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                    WebUtils.getMessage("product.delete.success"))
        }
        return "redirect:/products"
    }

}
