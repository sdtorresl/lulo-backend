package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class ProductResourceTest : BaseIT() {

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/productData.sql"
    )
    fun getAllProducts_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1400))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/productData.sql"
    )
    fun getAllProducts_filtered() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products?filter=1401")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(1401))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/productData.sql"
    )
    fun getProduct_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/1400")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value("Duis autem vel."))
    }

    @Test
    fun getProduct_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/2066")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    @Sql("/data/fileData.sql")
    fun createProduct_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/productDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, productRepository.count())
    }

    @Test
    fun createProduct_missingField() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/productDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.fieldErrors[0].field").value("name"))
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/productData.sql"
    )
    fun updateProduct_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/1400")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/productDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals("Nam liber tempor.", productRepository.findById(1400).get().name)
        Assertions.assertEquals(2, productRepository.count())
    }

    @Test
    @Sql(
        "/data/fileData.sql",
        "/data/productData.sql"
    )
    fun deleteProduct_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/1400")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, productRepository.count())
    }

}
