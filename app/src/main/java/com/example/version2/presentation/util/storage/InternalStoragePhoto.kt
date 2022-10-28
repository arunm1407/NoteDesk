package com.example.version2.presentation.util.storage

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class InternalStoragePhoto(
    var name: String,
    val bmp: Bitmap
):Parcelable