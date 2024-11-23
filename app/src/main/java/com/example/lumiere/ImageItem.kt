package com.example.lumiere

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageItem(
    val id:Int?=null,
    val image:String?=null) : Parcelable
