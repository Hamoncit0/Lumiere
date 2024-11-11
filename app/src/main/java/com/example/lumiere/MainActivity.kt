package com.example.lumiere

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lumiere.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        // Verificar si el usuario ya está logueado
        val sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Si ya está logueado, redirige a HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()  // Finaliza MainActivity para que no vuelva con el botón de retroceso
        }

        val signupBtn = binding.button2

        signupBtn.setOnClickListener { goToSignup() }


        val logInBtn = binding.button
        logInBtn.setOnClickListener { goToLogin() }
        enableEdgeToEdge()
        setContentView(binding.root)


    }
    fun goToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    fun goToSignup(){

        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}