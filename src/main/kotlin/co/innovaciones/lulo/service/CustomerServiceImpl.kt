package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.Customer
import co.innovaciones.lulo.model.CustomerDTO
import co.innovaciones.lulo.model.SimplePage
import co.innovaciones.lulo.repos.AddressRepository
import co.innovaciones.lulo.repos.BusinessRepository
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.repos.OrderRepository
import co.innovaciones.lulo.repos.PetRepository
import co.innovaciones.lulo.repos.UserRepository
import co.innovaciones.lulo.util.NotFoundException
import co.innovaciones.lulo.util.WebUtils
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository,
    private val businessRepository: BusinessRepository,
    private val petRepository: PetRepository,
    private val orderRepository: OrderRepository,
    private val addressRepository: AddressRepository
) : CustomerService {

    override fun findAll(filter: String?, pageable: Pageable): SimplePage<CustomerDTO> {
        var page: Page<Customer>
        if (filter != null) {
            page = customerRepository.findAllById(filter.toLongOrNull(), pageable)
        } else {
            page = customerRepository.findAll(pageable)
        }
        return SimplePage(page.content
                .stream()
                .map { customer -> mapToDTO(customer, CustomerDTO()) }
                .toList(),
                page.totalElements, pageable)
    }

    override fun `get`(id: Long): CustomerDTO = customerRepository.findById(id)
            .map { customer -> mapToDTO(customer, CustomerDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(customerDTO: CustomerDTO): Long {
        val customer = Customer()
        mapToEntity(customerDTO, customer)
        return customerRepository.save(customer).id!!
    }

    override fun update(id: Long, customerDTO: CustomerDTO) {
        val customer = customerRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(customerDTO, customer)
        customerRepository.save(customer)
    }

    override fun delete(id: Long) {
        val customer = customerRepository.findById(id)
                .orElseThrow { NotFoundException() }
        // remove many-to-many relations at owning side
        businessRepository.findAllByCustomers(customer)
                .forEach { business -> business.customers!!.remove(customer) }
        customerRepository.delete(customer)
    }

    private fun mapToDTO(customer: Customer, customerDTO: CustomerDTO): CustomerDTO {
        customerDTO.id = customer.id
        customerDTO.user = customer.user?.id
        return customerDTO
    }

    private fun mapToEntity(customerDTO: CustomerDTO, customer: Customer): Customer {
        val user = if (customerDTO.user == null) null else
                userRepository.findById(customerDTO.user!!)
                .orElseThrow { NotFoundException("user not found") }
        customer.user = user
        return customer
    }

    override fun userExists(id: Long?): Boolean = customerRepository.existsByUserId(id)

    override fun getReferencedWarning(id: Long): String? {
        val customer = customerRepository.findById(id)
                .orElseThrow { NotFoundException() }
        val ownerPet = petRepository.findFirstByOwner(customer)
        if (ownerPet != null) {
            return WebUtils.getMessage("customer.pet.owner.referenced", ownerPet.id)
        }
        val customerOrder = orderRepository.findFirstByCustomer(customer)
        if (customerOrder != null) {
            return WebUtils.getMessage("customer.order.customer.referenced", customerOrder.id)
        }
        val customersBusiness = businessRepository.findFirstByCustomers(customer)
        if (customersBusiness != null) {
            return WebUtils.getMessage("customer.business.customers.referenced",
                    customersBusiness.id)
        }
        val customerAddress = addressRepository.findFirstByCustomer(customer)
        if (customerAddress != null) {
            return WebUtils.getMessage("customer.address.customer.referenced", customerAddress.id)
        }
        return null
    }

}
