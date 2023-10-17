package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.Address
import co.innovaciones.lulo.model.AddressDTO
import co.innovaciones.lulo.repos.AddressRepository
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.repos.OrderRepository
import co.innovaciones.lulo.util.NotFoundException
import co.innovaciones.lulo.util.WebUtils
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service


@Service
class AddressServiceImpl(
    private val addressRepository: AddressRepository,
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository
) : AddressService {

    override fun findAll(): List<AddressDTO> {
        val addresss = addressRepository.findAll(Sort.by("id"))
        return addresss.stream()
                .map { address -> mapToDTO(address, AddressDTO()) }
                .toList()
    }

    override fun `get`(id: Long): AddressDTO = addressRepository.findById(id)
            .map { address -> mapToDTO(address, AddressDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(addressDTO: AddressDTO): Long {
        val address = Address()
        mapToEntity(addressDTO, address)
        return addressRepository.save(address).id!!
    }

    override fun update(id: Long, addressDTO: AddressDTO) {
        val address = addressRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(addressDTO, address)
        addressRepository.save(address)
    }

    override fun delete(id: Long) {
        addressRepository.deleteById(id)
    }

    private fun mapToDTO(address: Address, addressDTO: AddressDTO): AddressDTO {
        addressDTO.id = address.id
        addressDTO.address = address.address
        addressDTO.line1 = address.line1
        addressDTO.line2 = address.line2
        addressDTO.city = address.city
        addressDTO.province = address.province
        addressDTO.country = address.country
        addressDTO.postalCode = address.postalCode
        addressDTO.customer = address.customer?.id
        return addressDTO
    }

    private fun mapToEntity(addressDTO: AddressDTO, address: Address): Address {
        address.address = addressDTO.address
        address.line1 = addressDTO.line1
        address.line2 = addressDTO.line2
        address.city = addressDTO.city
        address.province = addressDTO.province
        address.country = addressDTO.country
        address.postalCode = addressDTO.postalCode
        val customer = if (addressDTO.customer == null) null else
                customerRepository.findById(addressDTO.customer!!)
                .orElseThrow { NotFoundException("customer not found") }
        address.customer = customer
        return address
    }

    override fun getReferencedWarning(id: Long): String? {
        val address = addressRepository.findById(id)
                .orElseThrow { NotFoundException() }
        val deliveryAddressOrder = orderRepository.findFirstByDeliveryAddress(address)
        if (deliveryAddressOrder != null) {
            return WebUtils.getMessage("address.order.deliveryAddress.referenced",
                    deliveryAddressOrder.id)
        }
        return null
    }

}
