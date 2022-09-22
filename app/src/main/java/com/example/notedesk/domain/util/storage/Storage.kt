package com.example.notedesk.domain.util.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.notedesk.domain.util.keys.Keys.CANNOT_SAVE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


object Storage {


    fun deletePhotoFromInternalStorage(filename: String, context: Context): Boolean {
        return try {
            context.deleteFile(filename)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    fun savePhotoToInternalStorage(
        filename: String,
        bmp: Bitmap,
        requireActivity: FragmentActivity
    ): Boolean {
        return try {
            requireActivity.openFileOutput("$filename.jpg", AppCompatActivity.MODE_PRIVATE)
                .use { stream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                        throw IOException(CANNOT_SAVE)
                    }
                }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


    private suspend fun loadPhotosFromInternalStorage(requireActivity: FragmentActivity): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = requireActivity.filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }


    suspend fun getPhotosFromInternalStorage(
        requireActivity: FragmentActivity,
        name: String
    ): InternalStoragePhoto? {
        var internalStoragePhoto: InternalStoragePhoto? = null
        loadPhotosFromInternalStorage(requireActivity).forEach {
            if (name == it.name)
                internalStoragePhoto = it
        }
        return internalStoragePhoto
    }


}