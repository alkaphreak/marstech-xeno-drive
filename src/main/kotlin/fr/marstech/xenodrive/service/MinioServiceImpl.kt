package fr.marstech.xenodrive.service

import io.minio.*
import io.minio.messages.Item
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import kotlin.io.path.fileSize
import kotlin.io.path.inputStream

@Service
class MinioServiceImpl(
    @Value("\${minio.endpoint}") val minioEndpoint: String,
    @Value("\${minio.accessKey}") val minioAccessKey: String,
    @Value("\${minio.secretKey}") val minioSecretKey: String,
    @Value("\${minio.bucket}") val minioBucket: String,
) : MinioService {

    var client: MinioClient? = null

    override fun connect(): MinioClient = when (client) {
        null -> MinioClient.builder()
            .endpoint(minioEndpoint)
            .credentials(minioAccessKey, minioSecretKey)
            .build()
            .also { client = it }

        else -> client!!
    }

    override fun downloadFile(remoteFilePath: String): GetObjectResponse? = connect().getObject(
        GetObjectArgs
            .builder()
            .bucket(minioBucket)
            .`object`(remoteFilePath)
            .build()
    )

    override fun uploadFile(
        localFilePath: Path,
        remoteFilePath: Path,
        contentType: String?
    ): ObjectWriteResponse? {
        return connect()
            .also {
                if (!isBucketExists(minioBucket)) createBucket(minioBucket)
            }
            .putObject(PutObjectArgs
                .builder()
                .bucket(minioBucket)
                .`object`(remoteFilePath.toString())
                .stream(
                    localFilePath.inputStream(),
                    localFilePath.fileSize(),
                    -1
                ).also {
                    if (!contentType.isNullOrEmpty()) it.contentType(contentType)
                }
                .build()
            )
    }

    private fun isBucketExists(bucketName: String): Boolean = connect()
        .bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())

    private fun createBucket(bucketName: String): Unit = connect()
        .makeBucket(
            MakeBucketArgs.builder().bucket(bucketName).build()
        )

    override fun listDirectory(remoteDirectoryPath: Path?): List<Result<Item>> {
        return connect()
            .listObjects(
                ListObjectsArgs
                    .builder()
                    .bucket(minioBucket)
                    .recursive(true)
                    .also {
                        if (remoteDirectoryPath != null) it.prefix(remoteDirectoryPath.toString())
                    }
                    .build()
            ).toList()
    }

    override fun remove() {
        TODO("Not yet implemented")
    }
}
