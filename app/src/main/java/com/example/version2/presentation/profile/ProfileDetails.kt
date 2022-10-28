package com.example.version2.presentation.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProfileDetails(val image: Int, val title: String, val content: String) : Parcelable
