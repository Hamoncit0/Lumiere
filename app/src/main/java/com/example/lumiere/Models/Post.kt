package com.example.lumiere.Models

data class Post(
    var id: Int,
    var user_id: Int,
    var category_id: Int,
    var image:String? =  null,
    var title:String? = null,
    var status:Int? = null,
    var created_at:String? = null,
    var username: String? = null
    )

//CLASE OG DE POSTS
//class Post(
//    val imageURL: String,
//    val username: String,
//    val description: String,
//    var imgArray:String? =  null)