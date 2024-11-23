package com.example.lumiere.Models

import android.os.Parcelable
import com.example.lumiere.ImageItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    var id: Int? =  null,
    var user_id: Int? =  null,
    var category_id: Int? =  null,
    var image:String? =  null,
    var title:String? = null,
    var status:Int? = null, //1 activo, 2 borrador, 3 eliminado
    var created_at:String? = null,
    var username: String? = null,
    var images: List<String> = listOf()
    ) : Parcelable

//CLASE OG DE POSTS
//class Post(
//    val imageURL: String,
//    val username: String,
//    val description: String,
//    var imgArray:String? =  null)