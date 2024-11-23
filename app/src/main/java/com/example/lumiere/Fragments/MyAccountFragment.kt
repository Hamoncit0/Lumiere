package com.example.lumiere.Fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lumiere.Models.Post
import com.example.lumiere.EditAccountActivity
import com.example.lumiere.MainActivity
import com.example.lumiere.Models.User
import com.example.lumiere.PostAdapter
import com.example.lumiere.RestEngine
import com.example.lumiere.Service
import com.example.lumiere.databinding.FragmentMyAccountBinding
import com.example.lumiere.responseBody.PostRB
import com.example.lumiere.responseBody.UserRB
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccountFragment : Fragment() {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!
    var user = User()
    var userId: Int = 0
    // Lista de álbumes y adaptador para el RecyclerView
    private lateinit var postList: List<Post>
    private lateinit var postAdapter: PostAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logOutBtn.setOnClickListener {
            logOut()
        }
        val editAccountButton = binding.imageButton
        editAccountButton.setOnClickListener {
            val intent = Intent(requireContext(), EditAccountActivity::class.java)
            intent.putExtra("UserData", user)
            startActivity(intent)
        }

        getPostsById()
        getUserInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun logOut() {
        val userPreferences = requireContext().getSharedPreferences("USER_PREF", MODE_PRIVATE)
        val localStorage = requireContext().getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)

        // Limpia los datos de sesión
        val userEditor = userPreferences.edit()
        userEditor.clear()
        userEditor.apply()

        // Limpia los drafts guardados localmente
        val localEditor = localStorage.edit()
        localEditor.remove("drafts") // Borra específicamente los drafts
        localEditor.apply()

        // Redirige al usuario a la pantalla de inicio de sesión
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    fun getPostsById(){
        val sharedPreferences = requireContext().getSharedPreferences("USER_PREF", MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)
        if(userId != 0){
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<PostRB> = service.getPostsByUserId(userId)

            result.enqueue(object : Callback<PostRB> {
                override fun onFailure(call: Call<PostRB>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: " + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PostRB>, response: Response<PostRB>) {
                    val postRB = response.body();
                    val posts = postRB?.list ?: emptyList()
                    postList = posts.filter {it.status == 1} // Guarda la lista de posts
                    postAdapter = PostAdapter(postList.toMutableList(), userId) // Actualiza el adaptador con los posts cargados
                    binding.recyclerViewAccount.adapter = postAdapter // Asigna el adaptador al RecyclerView
                    Toast.makeText(requireContext(), "Posts cargados", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun getUserInfo(){
        val sharedPreferences = requireContext().getSharedPreferences("USER_PREF", MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)
        if(userId != 0 && isInternetAvailable(requireContext())){
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<UserRB> = service.getUserById(userId)

            result.enqueue(object : Callback<UserRB> {
                override fun onFailure(call: Call<UserRB>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: " + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<UserRB>, response: Response<UserRB>) {
                    val userRB = response.body();
                    user = userRB?.user!!
                    val status:String = userRB?.status.toString()
                    if(status == "success"){
                        Toast.makeText(requireContext(),"Information loaded successfully", Toast.LENGTH_SHORT).show()
                        //val image = userRB?.user?.profile_picture
                        binding.textView.text = userRB?.user?.username

                        // Decodifica la imagen desde base64
                        val strImage = userRB?.user?.profile_picture?.replace("data:image/png;base64,", "")
                        if (strImage != null) {
                            try {
                                val byteArray = Base64.decode(strImage, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                                val circularBitmap = getCircularBitmap(bitmap)

                                // Establece la imagen circular en el ImageView
                                binding.imageView5.setImageBitmap(circularBitmap)
                                //binding.imageView5.setImageBitmap(bitmap)
                            } catch (e: IllegalArgumentException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    else{
                        Toast.makeText(requireContext(),"Error", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }else if(userId != 0){
            val username = sharedPreferences.getString("username", "username")
            val profilePicture = sharedPreferences.getString("profilePicture", null)
            binding.textView.text = username
            // Decodifica la imagen desde base64
            val strImage = profilePicture?.replace("data:image/png;base64,", "")
            if (strImage != null) {
                try {
                    val byteArray = Base64.decode(strImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    val circularBitmap = getCircularBitmap(bitmap)

                    // Establece la imagen circular en el ImageView
                    binding.imageView5.setImageBitmap(circularBitmap)
                    //binding.imageView5.setImageBitmap(bitmap)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
    }
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        val squareBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)
        val paint = Paint()
        val shader = BitmapShader(squareBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        paint.shader = shader

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        return output
    }
}