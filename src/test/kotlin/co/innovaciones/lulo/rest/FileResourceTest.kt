package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class FileResourceTest : BaseIT() {

    @Test
    @Sql("/data/fileData.sql")
    fun getAllFiles_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").value(1500))
    }

    @Test
    @Sql("/data/fileData.sql")
    fun getFile_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/1500")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value("Duis autem vel."))
    }

    @Test
    fun getFile_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/2166")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    fun createFile_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/files")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/fileDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(1, fileRepository.count())
    }

    @Test
    fun createFile_missingField() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/files")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/fileDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.fieldErrors[0].field").value("url"))
    }

    @Test
    @Sql("/data/fileData.sql")
    fun updateFile_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/files/1500")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/fileDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals("Nam liber tempor.", fileRepository.findById(1500).get().name)
        Assertions.assertEquals(2, fileRepository.count())
    }

    @Test
    @Sql("/data/fileData.sql")
    fun deleteFile_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/files/1500")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, fileRepository.count())
    }

}
