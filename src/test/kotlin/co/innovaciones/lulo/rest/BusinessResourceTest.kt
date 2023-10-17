package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class BusinessResourceTest : BaseIT() {

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql"
    )
    fun getAllBusinesss_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/businesss")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1200))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql"
    )
    fun getAllBusinesss_filtered() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/businesss?filter=1201")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1201))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql"
    )
    fun getBusiness_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/businesss/1200")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value("Duis autem vel."))
    }

    @Test
    fun getBusiness_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/businesss/1866")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    @Sql("/data/fileData.sql")
    fun createBusiness_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/businesss")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/businessDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, businessRepository.count())
    }

    @Test
    fun createBusiness_missingField() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/businesss")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/businessDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.fieldErrors[0].field").value("name"))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql"
    )
    fun updateBusiness_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/businesss/1200")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/businessDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals("Nam liber tempor.", businessRepository.findById(1200).get().name)
        Assertions.assertEquals(2, businessRepository.count())
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql"
    )
    fun deleteBusiness_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/businesss/1200")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, businessRepository.count())
    }

}
