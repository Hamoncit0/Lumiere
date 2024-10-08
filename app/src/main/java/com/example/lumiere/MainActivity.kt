package com.example.lumiere

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lumiere.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
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