package com.example.realestatemanager.data.content.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import com.example.realestatemanager.data.estate.room.EstateDao
import com.example.realestatemanager.data.media.room.MediaDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class ContentProvider: ContentProvider() {

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI(AUTHORITY, "estates", ESTATES)
        uriMatcher.addURI(AUTHORITY, "medias", MEDIAS)
        uriMatcher.addURI(AUTHORITY, "estates/#", ESTATE_BY_ID)
    }

    companion object {
        private const val AUTHORITY =
            "com.example.realestatemanager.data.content_provider.ContentProvider"
        private const val TABLE_NAME = "RealEstateManager_database"
        private const val MIME_TYPE_PREFIX = "vnd.android.cursor.dir/vnd."

        private const val ESTATES = 1
        private const val MEDIAS = 2
        private const val ESTATE_BY_ID = 3
    }

    @Inject
    lateinit var entryPoint: ContentProviderEntryPoint
    lateinit var estateDao: EstateDao
    lateinit var mediaDao: MediaDao

    override fun onCreate(): Boolean {
        val appContext = context?.applicationContext ?: throw IllegalStateException("EstateDao is null")
        val hiltEntryPoint = EntryPointAccessors.fromApplication(appContext, ContentProviderEntryPoint::class.java)
        estateDao = hiltEntryPoint.getEstateDao()
        mediaDao = hiltEntryPoint.getMediaDao()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val match = uriMatcher.match(uri)
        return try {
            when (match) {
                ESTATES -> estateDao.getAllEstatesWithCursor()
                MEDIAS -> mediaDao.getAllMediasWithCursor()
                ESTATE_BY_ID -> estateDao.getEstateByIdWithCursor(estateId = uri.lastPathSegment!!.toLong())
                else -> throw IllegalArgumentException("Unknown URI: $uri")
            }.apply {
                setNotificationUri(context?.contentResolver, uri)
            }
        } catch (e: SQLiteException) {
            Log.e("ContentProvider", "Error querying URI: $uri", e)
            MatrixCursor(arrayOf("error_message")).apply {
                addRow(arrayOf(e.message ?: "Unknown error!"))
            }
        }
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            ESTATES -> "$MIME_TYPE_PREFIX$AUTHORITY.$TABLE_NAME"
            MEDIAS -> "$MIME_TYPE_PREFIX$AUTHORITY.$TABLE_NAME"
            ESTATE_BY_ID -> "$MIME_TYPE_PREFIX$AUTHORITY.$TABLE_NAME"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int =
        0

    @EntryPoint  // runtime dependency graph
    @InstallIn(SingletonComponent::class)
    interface ContentProviderEntryPoint {
        fun getEstateDao(): EstateDao
        fun getMediaDao(): MediaDao
    }
}