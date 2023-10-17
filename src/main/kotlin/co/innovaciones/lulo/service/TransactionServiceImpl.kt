package co.innovaciones.lulo.service

import co.innovaciones.lulo.domain.Transaction
import co.innovaciones.lulo.model.TransactionDTO
import co.innovaciones.lulo.repos.OrderRepository
import co.innovaciones.lulo.repos.TransactionRepository
import co.innovaciones.lulo.util.NotFoundException
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service


@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val orderRepository: OrderRepository
) : TransactionService {

    override fun findAll(filter: String?): List<TransactionDTO> {
        var transactions: List<Transaction>
        val sort = Sort.by("id")
        if (filter != null) {
            transactions = transactionRepository.findAllById(filter.toLongOrNull(), sort)
        } else {
            transactions = transactionRepository.findAll(sort)
        }
        return transactions.stream()
                .map { transaction -> mapToDTO(transaction, TransactionDTO()) }
                .toList()
    }

    override fun `get`(id: Long): TransactionDTO = transactionRepository.findById(id)
            .map { transaction -> mapToDTO(transaction, TransactionDTO()) }
            .orElseThrow { NotFoundException() }

    override fun create(transactionDTO: TransactionDTO): Long {
        val transaction = Transaction()
        mapToEntity(transactionDTO, transaction)
        return transactionRepository.save(transaction).id!!
    }

    override fun update(id: Long, transactionDTO: TransactionDTO) {
        val transaction = transactionRepository.findById(id)
                .orElseThrow { NotFoundException() }
        mapToEntity(transactionDTO, transaction)
        transactionRepository.save(transaction)
    }

    override fun delete(id: Long) {
        transactionRepository.deleteById(id)
    }

    private fun mapToDTO(transaction: Transaction, transactionDTO: TransactionDTO): TransactionDTO {
        transactionDTO.id = transaction.id
        transactionDTO.session = transaction.session
        transactionDTO.content = transaction.content
        transactionDTO.order = transaction.order?.id
        return transactionDTO
    }

    private fun mapToEntity(transactionDTO: TransactionDTO, transaction: Transaction): Transaction {
        transaction.session = transactionDTO.session
        transaction.content = transactionDTO.content
        val order = if (transactionDTO.order == null) null else
                orderRepository.findById(transactionDTO.order!!)
                .orElseThrow { NotFoundException("order not found") }
        transaction.order = order
        return transaction
    }

    override fun sessionExists(session: String?): Boolean =
            transactionRepository.existsBySessionIgnoreCase(session)

}
