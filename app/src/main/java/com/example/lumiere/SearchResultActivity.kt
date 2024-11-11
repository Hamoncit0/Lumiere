package com.example.lumiere

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lumiere.Models.Post
import com.example.lumiere.databinding.ActivitySearchResultBinding

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar : Toolbar = binding.editAccountToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // Vuelve a la actividad anterior
        }

        // Obtener datos del intent
        val posts = intent.getSerializableExtra("posts") as? ArrayList<Post> ?: arrayListOf()
        val query = intent.getStringExtra("query") ?: ""
        val category = intent.getStringExtra("category") ?: ""

        // Mostrar datos en los TextView
        binding.textView16.text = "Search results for: $query in category: $category"

        val sharedPreferences = getSharedPreferences("USER_PREF", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0) // null es el valor por defecto

        // Configurar RecyclerView
        binding.recyclerViewSR.adapter = PostAdapter(posts, userId)
    }
}