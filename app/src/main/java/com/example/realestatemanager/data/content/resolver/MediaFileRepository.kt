package com.example.realestatemanager.data.content.resolver

interface MediaFileRepository {
    suspend fun saveToAppFiles(stringUri: String, filePrefix: String, suffix: String): String?
    suspend fun deleteFromAppFiles(absolutePath: String)
}
