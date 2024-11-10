package com.example.lumiere

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.lumiere.databinding.ActivityEditAccountBinding
import com.example.lumiere.responseBody.UserRB
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAccountActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditAccountBinding
    var userId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar : Toolbar = binding.editAccountToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // Vuelve a la actividad anterior
        }
        getUserInfo()
    }

    fun editAccount(){

    }

    fun getUserInfo(){
        val sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0)

        if(userId != 0){

            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<UserRB> = service.getUserById(userId)

            result.enqueue(object : Callback<UserRB> {
                override fun onFailure(call: Call<UserRB>, t: Throwable) {
                    Toast.makeText(this@EditAccountActivity, "Error: " + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<UserRB>, response: Response<UserRB>) {
                    val userRB = response.body();
                    val status:String = userRB?.status.toString()
                    if(status == "success"){

                        //val image = userRB?.user?.profile_picture
                        binding.apply {
                            editTextUserName.setText(userRB?.user?.username)
                            editTextFirstName.setText(userRB?.user?.first_name)
                            editTextLastName.setText(userRB?.user?.last_name)
                            editTextPassword3.setText(userRB?.user?.password)
                        }
                        // Decodifica la imagen desde base64
                        val strImage = userRB?.user?.profile_picture?.replace("data:image/png;base64,", "")
                        if (!strImage.isNullOrEmpty()) {
                            try {
                                val byteArray = Base64.decode(strImage, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                                binding.imageView7.setImageBitmap(bitmap)
                            } catch (e: IllegalArgumentException) {
                                e.printStackTrace()
                            }
                        }

                        Toast.makeText(this@EditAccountActivity,"Information loaded successfully", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this@EditAccountActivity,"Error", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
    }
}