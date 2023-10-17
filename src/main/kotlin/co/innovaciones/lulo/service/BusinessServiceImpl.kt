package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.Business
import co.innovaciones.lulo.model.BusinessDTO
import co.innovaciones.lulo.model.SimplePage
import co.innovaciones.lulo.repos.BusinessRepository
import co.innovaciones.lulo.repos.BusinessUserRepository
import co.innovaciones.lulo.repos.CategoryRepository
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.util.NotFoundException
import co.innovaciones.lulo.util.WebUtils
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
@Transactional
class BusinessServiceImpl(
    private val businessRepository: BusinessRepository,
    private val fileRepository: FileRepository,
    private val customerRepository: CustomerRepository,
    private val businessUserRepository: BusinessUserRepository,
    private val categoryRepository: CategoryRepository
) : BusinessService {

    override fun findAll(filter: String?, pageable: Pageable): SimplePage<BusinessDTO> {
        var page: Page<Business>
        if (filter != null) {
            page = businessRepository.findAllById(filter.toLongOrNull(), pageable)
        } else {
            page = businessRepository.findAll(pageable)
        }
        return SimplePage(page.content
                .stream()
                .map { business -> mapToDTO(business, BusinessDTO()) }
                .toList(),
                page.totalElements, pageable)
    }

    override fun `get`(id: Long): BusinessDTO = businessRepository.findById(id)
            .map { business -> mapToDTO(business, BusinessDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(businessDTO: BusinessDTO): Long {
        val business = Business()
        mapToEntity(businessDTO, business)
        return businessRepository.save(business).id!!
    }

    override fun update(id: Long, businessDTO: BusinessDTO) {
        val business = businessRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(businessDTO, business)
        businessRepository.save(business)
    }

    override fun delete(id: Long) {
        businessRepository.deleteById(id)
    }

    private fun mapToDTO(business: Business, businessDTO: BusinessDTO): BusinessDTO {
        businessDTO.id = business.id
        businessDTO.name = business.name
        businessDTO.description = business.description
        businessDTO.logo = business.logo?.id
        businessDTO.customers = business.customers?.stream()
                ?.map { customer -> customer.id!! }
                ?.toList()
        return businessDTO
    }

    private fun mapToEntity(businessDTO: BusinessDTO, business: Business): Business {
        business.name = businessDTO.name
        business.description = businessDTO.description
        val logo = if (businessDTO.logo == null) null else
                fileRepository.findById(businessDTO.logo!!)
                .orElseThrow { NotFoundException("logo not found") }
        business.logo = logo
        val customers = customerRepository.findAllById(businessDTO.customers ?: emptyList())
        if (customers.size != (if (businessDTO.customers == null) 0 else
                businessDTO.customers!!.size)) {
            throw NotFoundException("one of customers not found")
        }
        business.customers = customers.toMutableSet()
        return business
    }

    override fun logoExists(id: Long?): Boolean = businessRepository.existsByLogoId(id)

    override fun getReferencedWarning(id: Long): String? {
        val business = businessRepository.findById(id)
                .orElseThrow { NotFoundException() }
        val brandBusinessUser = businessUserRepository.findFirstByBrand(business)
        if (brandBusinessUser != null) {
            return WebUtils.getMessage("business.businessUser.brand.referenced",
                    brandBusinessUser.id)
        }
        val businessCategory = categoryRepository.findFirstByBusiness(business)
        if (businessCategory != null) {
            return WebUtils.getMessage("business.category.business.referenced", businessCategory.id)
        }
        return null
    }

}
