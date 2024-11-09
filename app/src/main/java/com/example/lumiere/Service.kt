package com.example.lumiere
import com.example.lumiere.Models.Category
import com.example.lumiere.Models.Post
import com.example.lumiere.Models.User
import com.example.lumiere.responseBody.PostRB
import com.example.lumiere.responseBody.UserRB
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

//Retrofi usa una interface para hacer la petición hacia el servidor
interface Service{

    //Servicios para consumir el User
    @GET("posts")
    fun getPosts():Call<List<Post>>

    @GET("Album/Albums/{id}")
    fun getAlbum(@Path("id") id: Int): Call<List<User>>

    @Headers("Content-Type: application/json")
    @POST("users")
    fun signUp(@Body userData: User):Call<Int>
    @Headers("Content-Type: application/json")
    @POST("users/login")
    fun logIn(@Body userData: User):Call<UserRB>
    @Headers("Content-Type: application/json")
    @POST("users/user")
    fun getUser(@Body email: String):Call<UserRB>
    @Headers("Content-Type: application/json")
    @POST("posts")
    fun savePost(@Body postData: Post):Call<PostRB>
    @Headers("Content-Type: application/json")
    @GET("categories")
    fun getCategories():Call<List<Category>>

}