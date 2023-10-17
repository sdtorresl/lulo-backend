package co.innovaciones.lulo.config

import co.innovaciones.lulo.LuloApplication
import co.innovaciones.lulo.repos.AddressRepository
import co.innovaciones.lulo.repos.BusinessRepository
import co.innovaciones.lulo.repos.BusinessUserRepository
import co.innovaciones.lulo.repos.CategoryRepository
import co.innovaciones.lulo.repos.CustomerRepository
import co.innovaciones.lulo.repos.FileRepository
import co.innovaciones.lulo.repos.OrderItemRepository
import co.innovaciones.lulo.repos.OrderRepository
import co.innovaciones.lulo.repos.PetRepository
import co.innovaciones.lulo.repos.ProductRepository
import co.innovaciones.lulo.repos.TransactionRepository
import co.innovaciones.lulo.repos.UserRepository
import java.nio.charset.StandardCharsets
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlMergeMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.util.StreamUtils
import org.testcontainers.containers.PostgreSQLContainer


/**
 * Abstract base class to be extended by every IT test. Starts the Spring Boot context with a
 * Datasource connected to the Testcontainers Docker instance. The instance is reused for all tests,
 * with all data wiped out before each test.
 */
@SpringBootTest(
    classes = [LuloApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("it")
@Sql(
    "/data/clearAll.sql",
    "/data/userData.sql"
)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
abstract class BaseIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var petRepository: PetRepository

    @Autowired
    lateinit var businessUserRepository: BusinessUserRepository

    @Autowired
    lateinit var businessRepository: BusinessRepository

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var orderItemRepository: OrderItemRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var fileRepository: FileRepository

    @Autowired
    lateinit var addressRepository: AddressRepository

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var userRepository: UserRepository

    fun readResource(resourceName: String): String =
            StreamUtils.copyToString(this.javaClass.getResourceAsStream(resourceName),
            StandardCharsets.UTF_8)

    fun bearerToken(): String {
        // user bootify, expires 2040-01-01
        return "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiJib290aWZ5IiwiaXNzIjoiYm9vdGlmeSIsImlhdCI6MTY4OTM5MzE5NCwiZXhwIjoyMjA4OTg4ODAwfQ." +
                "81D2rMoqM49mpgUQETUOhXwo1tlrK-1dE4KGqG2_h7ELiV1_D017EEPLeNn8OWE7MnSsMa2fA02I6gV3S7QQfw"
    }


    companion object {

        @ServiceConnection
        val postgreSQLContainer = PostgreSQLContainer("postgres:15.3")

        init {
            postgreSQLContainer.withReuse(true)
                    .start()
        }

    }

}
