package fr.marstech.xenodrive.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldNotBe
import io.minio.MinioClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KLogging
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.LogUtils
import java.nio.file.Files
import java.util.*

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MinioServiceTest : StringSpec() {

    lateinit var minioService: MinioService

    override suspend fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

        val container: MinIOContainer = MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z")
            .withUserName("testuser")
            .withPassword("testpassword")
            .withReuse(true)
            .withLabel("reuse.UUID", UUID.randomUUID().toString())
            .withTmpFs(mapOf(withContext(Dispatchers.IO) {
                Files.createTempDirectory(
                    UUID.randomUUID().toString()
                )
            }.toString() to "rw"))
            .apply {
                start()
                LogUtils.followOutput(
                    dockerClient,
                    containerId,
                    Slf4jLogConsumer(logger).withSeparateOutputStreams()
                )
            }

        minioService = MinioServiceImpl(
            minioEndpoint = container.s3URL,
            minioAccessKey = container.userName,
            minioSecretKey = container.password
        )
    }

    init {
        "Should connect successfully" {
            // Given
            val client: MinioClient?

            // When
            client = minioService.connect()

            // Then
            client shouldNotBe null
        }

        "Should upload successfully " {
            // Given


            // When

            // Then

        }
    }

    companion object : KLogging()
}
