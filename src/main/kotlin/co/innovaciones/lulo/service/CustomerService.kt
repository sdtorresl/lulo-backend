package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.CustomerDTO
import co.innovaciones.lulo.model.SimplePage
import org.springframework.data.domain.Pageable


interface CustomerService {

    fun findAll(filter: String?, pageable: Pageable): SimplePage<CustomerDTO>

    fun `get`(id: Long): CustomerDTO

    fun create(customerDTO: CustomerDTO): Long

    fun update(id: Long, customerDTO: CustomerDTO)

    fun delete(id: Long)

    fun userExists(id: Long?): Boolean

    fun getReferencedWarning(id: Long): String?

}
