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
    @GET("Album/Albums/{id}")
    fun getAlbum(@Path("id") id: Int): Call<List<User>>

    ///USERS
    @Headers("Content-Type: application/json")
    @POST("users")
    fun signUp(@Body userData: User):Call<Int>
    @Headers("Content-Type: application/json")
    @POST("users/login")
    fun logIn(@Body userData: User):Call<UserRB>
    @GET("users/user/{userId}")
    fun getUserById(@Path("userId") userId: Int):Call<UserRB>
    @Headers("Content-Type: application/json")
    @POST("users/updateUser")
    fun updateUser(@Body userData: User):Call<UserRB>

    //POSTS
    @Headers("Content-Type: application/json")
    @POST("posts")
    fun savePost(@Body postData: Post):Call<PostRB>
    @GET("posts")
    fun getPosts():Call<List<Post>>
    @GET("posts/post/{userId}")
    fun getPostsByUserId(@Path("userId") userId: Int): Call<List<Post>>
    @Headers("Content-Type: application/json")
    @POST("posts/updatePost")
    fun updatePostStatus(@Body postData: Post):Call<PostRB>


    //CATEGORIES
    @Headers("Content-Type: application/json")
    @GET("categories")
    fun getCategories():Call<List<Category>>


}