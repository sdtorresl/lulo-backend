package co.innovaciones.lulo.controller

import co.innovaciones.lulo.domain.Business
import co.innovaciones.lulo.domain.File
import co.innovaciones.lulo.domain.Product
import co.innovaciones.lulo.model.CategoryDTO
import co.innovaciones.lulo.model.CategoryType
import co.innovaciones.lulo.repos.BusinessRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.repos.ProductRepository
import co.innovaciones.lulo.service.CategoryService
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
@RequestMapping("/categorys")
class CategoryController(
    private val categoryService: CategoryService,
    private val productRepository: ProductRepository,
    private val fileRepository: FileRepository,
    private val businessRepository: BusinessRepository
) {

    @ModelAttribute
    fun prepareContext(model: Model) {
        model.addAttribute("typeValues", CategoryType.values())
        model.addAttribute("productsValues", productRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Product::id, Product::name)))
        model.addAttribute("fileValues", fileRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(File::id, File::name)))
        model.addAttribute("businessValues", businessRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Business::id, Business::name)))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) filter: String?,
        @SortDefault(sort = ["id"]) @PageableDefault(size = 20) pageable: Pageable,
        model: Model
    ): String {
        val categorys = categoryService.findAll(filter, pageable)
        model.addAttribute("categorys", categorys)
        model.addAttribute("filter", filter)
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(categorys))
        return "category/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("category") categoryDTO: CategoryDTO): String = "category/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("category") @Valid categoryDTO: CategoryDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!bindingResult.hasFieldErrors("file") && categoryDTO.file != null &&
                categoryService.fileExists(categoryDTO.file)) {
            bindingResult.rejectValue("file", "Exists.category.file")
        }
        if (bindingResult.hasErrors()) {
            return "category/add"
        }
        categoryService.create(categoryDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("category.create.success"))
        return "redirect:/categorys"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("category", categoryService.get(id))
        return "category/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("category") @Valid categoryDTO: CategoryDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        val currentCategoryDTO: CategoryDTO = categoryService.get(id)
        if (!bindingResult.hasFieldErrors("file") && categoryDTO.file != null &&
                !categoryDTO.file!!.equals(currentCategoryDTO.file) &&
                categoryService.fileExists(categoryDTO.file)) {
            bindingResult.rejectValue("file", "Exists.category.file")
        }
        if (bindingResult.hasErrors()) {
            return "category/edit"
        }
        categoryService.update(id, categoryDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("category.update.success"))
        return "redirect:/categorys"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        categoryService.delete(id)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                WebUtils.getMessage("category.delete.success"))
        return "redirect:/categorys"
    }

}
