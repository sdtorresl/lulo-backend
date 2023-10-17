package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class PetResourceTest : BaseIT() {

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/petData.sql"
    )
    fun getAllPets_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/pets")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1000))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/petData.sql"
    )
    fun getAllPets_filtered() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/pets?filter=1001")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1001))
    }

    @Test
    fun getAllPets_unauthorized() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/pets")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("Unauthorized"))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/petData.sql"
    )
    fun getPet_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/pets/1000")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value("Duis autem vel."))
    }

    @Test
    fun getPet_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/pets/1666")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    @Sql("/data/customerData.sql")
    fun createPet_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pets")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/petDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, petRepository.count())
    }

    @Test
    fun createPet_missingField() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pets")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/petDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.fieldErrors[0].field").value("name"))
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/petData.sql"
    )
    fun updatePet_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/pets/1000")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/petDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals("Nam liber tempor.", petRepository.findById(1000).get().name)
        Assertions.assertEquals(2, petRepository.count())
    }

    @Test
    @Sql(
        "/data/customerData.sql",
        "/data/petData.sql"
    )
    fun deletePet_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/pets/1000")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, petRepository.count())
    }

}
