package co.innovaciones.lulo.util

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotNull
import java.lang.Math
import java.util.ArrayList
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.LocaleResolver


@Component
class WebUtils(
    messageSource: MessageSource,
    localeResolver: LocaleResolver
) {

    init {
        WebUtils.messageSource = messageSource
        WebUtils.localeResolver = localeResolver
    }

    companion object {

        const val MSG_SUCCESS = "MSG_SUCCESS"

        const val MSG_INFO = "MSG_INFO"

        const val MSG_ERROR = "MSG_ERROR"

        lateinit var messageSource: MessageSource

        lateinit var localeResolver: LocaleResolver

        @JvmStatic
        fun getRequest(): HttpServletRequest = (RequestContextHolder.getRequestAttributes() as
                ServletRequestAttributes).request

        @JvmStatic
        fun getMessage(code: String, vararg args: Any?): String? = messageSource.getMessage(code,
                args, code, localeResolver.resolveLocale(getRequest()))

        @JvmStatic
        fun isRequiredField(dto: Any, fieldName: String): Boolean =
                dto::class.memberProperties.first { it.name ==
                fieldName }.javaField!!.getAnnotation(NotNull::class.java) != null

        @JvmStatic
        private fun getStepUrl(page: Page<*>, targetPage: Int): String {
            var stepUrl = "?page=${targetPage}&size=${page.size}"
            if (getRequest().getParameter("filter") != null) {
                stepUrl += "&filter=" + getRequest().getParameter("filter")
            }
            return stepUrl
        }

        @JvmStatic
        fun getPaginationModel(page: Page<*>): PaginationModel? {
            if (page.isEmpty) {
                return null
            }

            val steps: ArrayList<PaginationStep> = ArrayList()
            var step = PaginationStep()
            step.disabled = !page.hasPrevious()
            step.label = getMessage("pagination.previous")
            step.url = getStepUrl(page, page.previousOrFirstPageable().pageNumber)
            steps.add(step)
            // find a range of up to 5 pages around the current active page
            val startAt = Math.max(0, Math.min(page.number - 2, page.totalPages - 5))
            val endAt = Math.min(startAt + 5, page.totalPages)
            for (i in startAt until endAt) {
                step = PaginationStep()
                step.active = i == page.number
                step.label = "" + (i + 1)
                step.url = getStepUrl(page, i)
                steps.add(step)
            }
            step = PaginationStep()
            step.disabled = !page.hasNext()
            step.label = getMessage("pagination.next")
            step.url = getStepUrl(page, page.nextOrLastPageable().pageNumber)
            steps.add(step)

            val startElements = page.number * page.size + 1L
            val endElements = Math.min(startElements + page.size - 1, page.totalElements)
            val range = if (startElements == endElements) "" + startElements else
                    "${startElements} - ${endElements}"
            val paginationModel = PaginationModel()
            paginationModel.steps = steps
            paginationModel.elements = getMessage("pagination.elements", range, page.totalElements)
            return paginationModel
        }

    }

}
