package fr.marstech.xenodrive.service

import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

@Service
class MinioService(
    @Value("\${minio.endpoint}")
    private val minioEndpoint: String,

    @Value("\${minio.accessKey}")
    private val minioAccessKey: String,

    @Value("\${minio.secretKey}")
    private val minioSecretKey: String
) {

    fun uploadFile(file: MultipartFile) {
        val minioClient = MinioClient.builder()
            .endpoint(minioEndpoint)
            .credentials(minioAccessKey, minioSecretKey)
            .build()

        val bucketName = "your-bucket-name"
        val objectName = file.originalFilename ?: "default.txt" // Use a default name if filename is empty

        val inputStream: InputStream = file.inputStream
        val size: Long = file.size

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectName)
                .stream(inputStream, size, -1)
                .contentType("text/plain")
                .build()
        )

        inputStream.close()
    }
}
