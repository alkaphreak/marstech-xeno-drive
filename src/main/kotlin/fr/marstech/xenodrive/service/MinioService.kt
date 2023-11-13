package fr.marstech.xenodrive.service

import io.minio.MinioClient

interface MinioService {
    fun connect(): MinioClient
    fun downloadFile()
    fun uploadFile()
    fun listDirectory()
    fun remove()
}
