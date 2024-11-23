package com.example.lumiere.Fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lumiere.Models.Post
import com.example.lumiere.PostAdapter
import com.example.lumiere.R
import com.example.lumiere.RestEngine
import com.example.lumiere.Service
import com.example.lumiere.databinding.FragmentDraftsBinding
import com.example.lumiere.databinding.FragmentHomeBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DraftsFragment : Fragment() {
    private var _binding: FragmentDraftsBinding? = null
    private val binding get() = _binding!!
    var userId: Int = 0
    // Lista de álbumes y adaptador para el RecyclerView
    private lateinit var postList: List<Post>
    private lateinit var postAdapter: PostAdapter
    private var draftsArray: MutableList<Post> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDraftsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa el RecyclerView y el adaptador
        val recyclerView = binding.recycleViewDrafts

        val sharedPreferences = requireContext().getSharedPreferences("USER_PREF", AppCompatActivity.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0) // null es el valor por defecto

        val emptyMutableList: MutableList<Post> = mutableListOf()

        loadDraftsLocally()
        postAdapter = PostAdapter(draftsArray.toMutableList(), userId) // Actualiza el adaptador con los borradores locales
        recyclerView.adapter = postAdapter // Asigna el adaptador al RecyclerView
        Toast.makeText(requireContext(), "Cargando drafts locales", Toast.LENGTH_LONG).show()

    }

    private fun loadDraftsLocally() {
        val sharedPreferences = requireContext().getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)
        val gson = Gson()

        // Lee el JSON de borradores
        val jsonDrafts = sharedPreferences.getString("drafts", null)

        // Verifica si hay datos guardados
        if (jsonDrafts != null) {
            // Convierte el JSON a ArrayList<Post>
            val type = object : TypeToken<ArrayList<Post>>() {}.type
            draftsArray = gson.fromJson(jsonDrafts, type)
        } else {
            draftsArray = ArrayList() // Si no hay datos, inicializa una lista vacía
        }
    }
    fun saveDraftsToSharedPreferences(holder: PostAdapter.ViewHolder) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val jsonDrafts = gson.toJson(draftsArray) // Convierte el array de borradores a JSON
        editor.putString("drafts", jsonDrafts)
        editor.apply()
    }


}