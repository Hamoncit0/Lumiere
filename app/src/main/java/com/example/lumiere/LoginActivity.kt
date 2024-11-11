package com.example.lumiere

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lumiere.Models.User
import com.example.lumiere.databinding.ActivityLoginBinding
import com.example.lumiere.responseBody.UserRB
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logInBtn = binding.button3
        logInBtn.setOnClickListener {
            logIn(binding)
        }
    }

    private fun logIn(binding: ActivityLoginBinding) {


        val user = User(
            email = binding.editTextTextEmailAddress.text.toString(),
            password = binding.editTextTextPassword2.text.toString()
        )

        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<UserRB> = service.logIn(user)

        result.enqueue(object : Callback<UserRB> {
            override fun onFailure(call: Call<UserRB>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: " + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<UserRB>, response: Response<UserRB>) {
                val userRB = response.body();
                val status:String = userRB?.status.toString()
                if(status == "success"){
                    Toast.makeText(this@LoginActivity,"Logged in successfully", Toast.LENGTH_LONG).show()
                    val userId = userRB?.user?.id ?: 0
                    // Guardar el estado de sesión
                    val sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.putInt("userId", userId)  // Puedes almacenar otros datos si es necesario
                    editor.apply()

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this@LoginActivity,"Error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}