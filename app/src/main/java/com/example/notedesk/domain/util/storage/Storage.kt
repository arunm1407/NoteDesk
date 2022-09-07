package com.example.notedesk.domain.util.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
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
                        throw IOException("Couldn't save bitmap.")
                    }
                }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getPhotosFromInternalStorage(filenames: List<String>,requireActivity: FragmentActivity): MutableList<InternalStoragePhoto> {
        val list = mutableListOf<InternalStoragePhoto>()
        filenames.forEach { name ->
            loadPhotosFromInternalStorage(requireActivity).forEach {
                if (name == it.name)
                    list.add(it)
            }
        }

        return list
    }


    private suspend fun loadPhotosFromInternalStorage(requireActivity: FragmentActivity): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = requireActivity.filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp:Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }


}