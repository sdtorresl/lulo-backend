package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class TransactionResourceTest : BaseIT() {

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql",
        "/data/transactionData.sql"
    )
    fun getAllTransactions_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").value(2000))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql",
        "/data/transactionData.sql"
    )
    fun getAllTransactions_filtered() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions?filter=2001")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").value(2001))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql",
        "/data/transactionData.sql"
    )
    fun getTransaction_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions/2000")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.session").value("Ut wisi enim."))
    }

    @Test
    fun getTransaction_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions/2666")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql"
    )
    fun createTransaction_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/transactionDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, transactionRepository.count())
    }

    @Test
    fun createTransaction_missingField() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/transactionDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.fieldErrors[0].field").value("session"))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql",
        "/data/transactionData.sql"
    )
    fun updateTransaction_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions/2000")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/transactionDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals("Sed ut perspiciatis.",
                transactionRepository.findById(2000).get().session)
        Assertions.assertEquals(2, transactionRepository.count())
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql",
        "/data/transactionData.sql"
    )
    fun deleteTransaction_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/transactions/2000")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, transactionRepository.count())
    }

}
