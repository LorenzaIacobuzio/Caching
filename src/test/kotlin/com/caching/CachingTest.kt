import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class CachingTest {
    companion object {
        private const val REDIS_CONFIG_HOST = "spring.data.redis.host"
        private const val REDIS_CONFIG_PORT = "spring.data.redis.port"

        private val redisContainer = GenericContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379)

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            redisContainer.start()
            redisContainer.setWaitStrategy(HostPortWaitStrategy())
            System.setProperty("spring.redis.host", redisContainer.host);
            System.setProperty("spring.redis.port", redisContainer.getMappedPort(6379).toString());
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add(
                REDIS_CONFIG_HOST,
                redisContainer::getContainerIpAddress
            )
            registry.add(
                REDIS_CONFIG_PORT,
                redisContainer::getFirstMappedPort
            )
        }
    }
}