package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class OrderResourceTest : BaseIT() {

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql"
    )
    fun getAllOrders_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").value(1700))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql"
    )
    fun getAllOrders_filtered() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders?filter=1701")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").value(1701))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql"
    )
    fun getOrder_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/1700")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.sessionId").value("Duis autem vel."))
    }

    @Test
    fun getOrder_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/2366")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    @Sql("/data/customerData.sql")
    fun createOrder_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/orderDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, orderRepository.count())
    }

    @Test
    fun createOrder_missingField() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/orderDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.fieldErrors[0].field").value("sessionId"))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql"
    )
    fun updateOrder_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/1700")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/orderDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals("Nam liber tempor.", orderRepository.findById(1700).get().sessionId)
        Assertions.assertEquals(2, orderRepository.count())
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/orderData.sql"
    )
    fun deleteOrder_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders/1700")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, orderRepository.count())
    }

}
