package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class CustomerResourceTest : BaseIT() {

    @Test
    @Sql("/data/customerData.sql")
    fun getAllCustomers_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1100))
    }

    @Test
    @Sql("/data/customerData.sql")
    fun getAllCustomers_filtered() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers?filter=1101")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1101))
    }

    @Test
    @Sql("/data/customerData.sql")
    fun getCustomer_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/1100")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    fun getCustomer_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/1766")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    fun createCustomer_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/customerDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, customerRepository.count())
    }

    @Test
    @Sql("/data/customerData.sql")
    fun updateCustomer_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/customers/1100")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/customerDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals(2, customerRepository.count())
    }

    @Test
    @Sql("/data/customerData.sql")
    fun deleteCustomer_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/customers/1100")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, customerRepository.count())
    }

}
