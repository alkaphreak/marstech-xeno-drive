package fr.marstech.xenodrive.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.minio.Result
import io.minio.messages.Item
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
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

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
            .withLabel("reuse.UUID", UUID_STR)
            .withTmpFs(mapOf(withContext(Dispatchers.IO) {
                Files.createTempDirectory(UUID_STR)
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
            minioSecretKey = container.password,
            minioBucket = UUID_STR
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

        "Should upload/list successfully " {
            // Given
            val localFilePath: Path = Files.createTempFile(
                Files.createTempDirectory("localFiles"),
                "localFile",
                null
            )
            val remoteFilePath: Path = Path("my-minio/my-sub-path")
                .resolve("${UUID.randomUUID()}.tmp")

            // When
            val writeResponse: ObjectWriteResponse? = minioService.uploadFile(
                localFilePath,
                remoteFilePath
            )
            val resultList: List<Result<Item>> = minioService.listDirectory(remoteFilePath.parent)

            // Then
            writeResponse != null
            writeResponse!!.bucket() shouldBe UUID_STR
            writeResponse.`object`() shouldBe remoteFilePath.toString()

            resultList.size shouldBe 1
            resultList[0].get().objectName() shouldBe remoteFilePath.toString()
        }
    }

    companion object : KLogging() {
        const val UUID_STR: String = "97f215fe-0763-4393-8a73-004d6c3899c2"
    }
}
