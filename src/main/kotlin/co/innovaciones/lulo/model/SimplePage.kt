package co.innovaciones.lulo.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort


@JsonIgnoreProperties("pageable", "number", "numberOfElements", "first", "last", "empty")
class SimplePage<T> : PageImpl<T> {

    @JsonCreator
    constructor(
        @JsonProperty("content") content: List<T>,
        @JsonProperty("totalElements") totalElements: Long,
        @JsonProperty("totalPages") @Suppress("UNUSED_PARAMETER") totalPages: Int,
        @JsonProperty("page") page: Int,
        @JsonProperty("size") size: Int,
        @JsonProperty("sort") sort: List<String>
    ) : super(content, PageRequest.of(page, size, Sort.by(sort.stream()
    .map { el -> el.split(",") }
    .map { ar -> Sort.Order(Sort.Direction.fromString(ar[1]), ar[0]) }
    .toList())), totalElements)

    constructor(
        content: List<T>,
        totalElements: Long,
        pageable: Pageable
    ) : super(content, pageable, totalElements)

    fun getPage(): Int = number

    @JsonProperty("sort")
    fun getSortList(): List<String> {
        return sort.stream()
                .map { order -> order.property + "," + order.direction.name }
                .toList()
    }

}
