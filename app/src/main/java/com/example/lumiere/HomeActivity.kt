package com.example.lumiere

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lumiere.Classes.Post

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val posts = listOf(
            Post("https://i.pinimg.com/236x/d5/39/17/d539174e175e07e8a374616766a44750.jpg",
                "Hamon", "Calacas"),
            Post("https://i.pinimg.com/236x/4d/ce/f6/4dcef66683e7c0eb7caf8cc59c687845.jpg",
                "Hamon", "yo ese"),
            Post("https://i.pinimg.com/236x/bc/2c/84/bc2c8473fc5651bb2a6d9ee16f31ea4c.jpg",
                "Hamon", "yo ese"),
            Post("https://i.pinimg.com/236x/fa/02/c5/fa02c54a71ec17b8a5273ccdb252dc30.jpg",
                "Hamon", "yo ese")
        )
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHome)

        recyclerView.adapter = PostAdapter(posts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}