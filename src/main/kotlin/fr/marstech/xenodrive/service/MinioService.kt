package fr.marstech.xenodrive.service

import io.minio.GetObjectResponse
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.minio.Result
import io.minio.messages.Item
import java.nio.file.Path

interface MinioService {
    fun connect(): MinioClient

    fun downloadFile(remoteFilePath: String): GetObjectResponse?

    fun uploadFile(
        localFilePath: Path,
        remoteFilePath: Path,
        contentType: String? = null
    ): ObjectWriteResponse?

    fun remove()

    fun listDirectory(remoteDirectoryPath: Path? = null): List<Result<Item>>
}
