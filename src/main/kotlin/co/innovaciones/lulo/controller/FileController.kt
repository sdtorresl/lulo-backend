package co.innovaciones.lulo.controller

import co.innovaciones.lulo.model.FileDTO
import co.innovaciones.lulo.service.FileService
import co.innovaciones.lulo.util.WebUtils
import jakarta.validation.Valid
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
@RequestMapping("/files")
class FileController(
    private val fileService: FileService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("files", fileService.findAll())
        return "file/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("file") fileDTO: FileDTO): String = "file/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("file") @Valid fileDTO: FileDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "file/add"
        }
        fileService.create(fileDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("file.create.success"))
        return "redirect:/files"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("file", fileService.get(id))
        return "file/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @ModelAttribute("file") @Valid fileDTO: FileDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "file/edit"
        }
        fileService.update(id, fileDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("file.update.success"))
        return "redirect:/files"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        val referencedWarning: String? = fileService.getReferencedWarning(id)
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning)
        } else {
            fileService.delete(id)
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                    WebUtils.getMessage("file.delete.success"))
        }
        return "redirect:/files"
    }

}
