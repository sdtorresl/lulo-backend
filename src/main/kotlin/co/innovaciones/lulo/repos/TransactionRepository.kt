package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.Order
import co.innovaciones.lulo.domain.Transaction
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository


interface TransactionRepository : JpaRepository<Transaction, Long> {

    fun findAllById(id: Long?, sort: Sort): List<Transaction>

    fun findFirstByOrder(order: Order): Transaction?

    fun existsBySessionIgnoreCase(session: String?): Boolean

}
