package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class CategoryResourceTest : BaseIT() {

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/categoryData.sql"
    )
    fun getAllCategorys_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/categorys")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1600))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/categoryData.sql"
    )
    fun getAllCategorys_filtered() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/categorys?filter=1601")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1601))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/categoryData.sql"
    )
    fun getCategory_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/categorys/1600")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value("Duis autem vel."))
    }

    @Test
    fun getCategory_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/categorys/2266")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    @Sql("/data/fileData.sql")
    fun createCategory_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/categorys")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/categoryDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, categoryRepository.count())
    }

    @Test
    fun createCategory_missingField() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/categorys")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/categoryDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.fieldErrors[0].field").value("name"))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/categoryData.sql"
    )
    fun updateCategory_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/categorys/1600")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/categoryDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals("Nam liber tempor.", categoryRepository.findById(1600).get().name)
        Assertions.assertEquals(2, categoryRepository.count())
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/categoryData.sql"
    )
    fun deleteCategory_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categorys/1600")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, categoryRepository.count())
    }

}
