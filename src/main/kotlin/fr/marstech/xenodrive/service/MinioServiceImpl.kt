package fr.marstech.xenodrive.service

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MinioServiceImpl(
    @Value("\${minio.endpoint}") val minioEndpoint: String,
    @Value("\${minio.accessKey}") val minioAccessKey: String,
    @Value("\${minio.secretKey}") val minioSecretKey: String
) : MinioService {

//    fun uploadFile(file: MultipartFile) {
//        val bucketName = "your-bucket-name"
//        val objectName = file.originalFilename ?: "default.txt" // Use a default name if filename is empty
//
//        val inputStream: InputStream = file.inputStream
//        val size: Long = file.size
//
//        minioClient.putObject(
//            PutObjectArgs.builder()
//                .bucket(bucketName)
//                .`object`(objectName)
//                .stream(inputStream, size, -1)
//                .contentType("text/plain")
//                .build()
//        )
//
//        inputStream.close()
//    }

    var client: MinioClient? = null

    override fun connect(): MinioClient = when (client) {
        null -> MinioClient.builder()
            .endpoint(minioEndpoint)
            .credentials(minioAccessKey, minioSecretKey)
            .build()
            .also { client = it }

        else -> client!!
    }

    override fun downloadFile() {
        TODO("Not yet implemented")
    }

    override fun uploadFile() {
        TODO("Not yet implemented")
    }

    override fun listDirectory() {
        TODO("Not yet implemented")
    }

    override fun remove() {
        TODO("Not yet implemented")
    }
}
