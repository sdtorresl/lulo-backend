package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class AddressResourceTest : BaseIT() {

    @Test
    @Sql("/data/addressData.sql")
    fun getAllAddresss_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/addresss")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").value(1900))
    }

    @Test
    @Sql("/data/addressData.sql")
    fun getAddress_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/addresss/1900")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.address").value("Nam liber tempor."))
    }

    @Test
    fun getAddress_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/addresss/2566")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    fun createAddress_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addresss")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/addressDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, addressRepository.count())
    }

    @Test
    fun createAddress_missingField() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/addresss")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/addressDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.fieldErrors[0].field").value("address"))
    }

    @Test
    @Sql("/data/addressData.sql")
    fun updateAddress_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/addresss/1900")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/addressDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals("Lorem ipsum dolor.",
                addressRepository.findById(1900).get().address)
        Assertions.assertEquals(2, addressRepository.count())
    }

    @Test
    @Sql("/data/addressData.sql")
    fun deleteAddress_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/addresss/1900")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, addressRepository.count())
    }

}
