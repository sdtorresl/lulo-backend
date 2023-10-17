package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.TransactionDTO


interface TransactionService {

    fun findAll(filter: String?): List<TransactionDTO>

    fun `get`(id: Long): TransactionDTO

    fun create(transactionDTO: TransactionDTO): Long

    fun update(id: Long, transactionDTO: TransactionDTO)

    fun delete(id: Long)

    fun sessionExists(session: String?): Boolean

}
