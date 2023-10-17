package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class BusinessUserResourceTest : BaseIT() {

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql",
        "/data/businessUserData.sql"
    )
    fun getAllBusinessUsers_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/businessUsers")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1300))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql",
        "/data/businessUserData.sql"
    )
    fun getAllBusinessUsers_filtered() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/businessUsers?filter=1301")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1301))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql",
        "/data/businessUserData.sql"
    )
    fun getBusinessUser_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/businessUsers/1300")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    fun getBusinessUser_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/businessUsers/1966")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql"
    )
    fun createBusinessUser_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/businessUsers")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/businessUserDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, businessUserRepository.count())
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql",
        "/data/businessUserData.sql"
    )
    fun updateBusinessUser_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/businessUsers/1300")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/businessUserDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals(2, businessUserRepository.count())
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/businessData.sql",
        "/data/businessUserData.sql"
    )
    fun deleteBusinessUser_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/businessUsers/1300")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, businessUserRepository.count())
    }

}
