package co.innovaciones.lulo.rest

import co.innovaciones.lulo.config.BaseIT
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class UserResourceTest : BaseIT() {

    @Test
    fun getAllUsers_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(2100))
    }

    @Test
    fun getAllUsers_filtered() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users?filter=2101")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content[0].id").value(2101))
    }

    @Test
    fun getUser_success() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/2100")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.firstName").value("Sed ut perspiciatis."))
    }

    @Test
    fun getUser_notFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/2766")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("NotFoundException"))
    }

    @Test
    fun createUser_success() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/userDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
        Assertions.assertEquals(3, userRepository.count())
    }

    @Test
    fun createUser_missingField() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/userDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("\$.exception").value("MethodArgumentNotValidException"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.fieldErrors[0].field").value("firstName"))
    }

    @Test
    fun updateUser_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/2100")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/userDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
        Assertions.assertEquals("Duis autem vel.", userRepository.findById(2100).get().firstName)
        Assertions.assertEquals(2, userRepository.count())
    }

    @Test
    fun deleteUser_success() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/2100")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        Assertions.assertEquals(1, userRepository.count())
    }

}
