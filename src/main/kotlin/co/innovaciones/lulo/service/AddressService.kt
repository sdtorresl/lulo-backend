package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.AddressDTO


interface AddressService {

    fun findAll(): List<AddressDTO>

    fun `get`(id: Long): AddressDTO

    fun create(addressDTO: AddressDTO): Long

    fun update(id: Long, addressDTO: AddressDTO)

    fun delete(id: Long)

    fun getReferencedWarning(id: Long): String?

}
