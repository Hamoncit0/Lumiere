package com.example.lumiere.Fragments

import android.content.Context
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
        postAdapter = PostAdapter(emptyMutableList, userId) // Inicializa con una lista vacía
        recyclerView.adapter = postAdapter

        // Cargar álbumes desde el servidor
        getPosts()

    }

    private fun getPosts() {
        val sharedPreferences = requireContext().getSharedPreferences("USER_PREF",
            Context.MODE_PRIVATE
        )
        val userId = sharedPreferences.getInt("userId", 0)

        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Post>> = service.getPostsByUserId(userId)

        result.enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: " + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val posts = response.body() ?: emptyList()
                postList = posts.filter { it.status == 2 } // Filtra los posts con status activo
                postAdapter = PostAdapter(postList.toMutableList(), userId) // Actualiza el adaptador con los posts cargados
                binding.recycleViewDrafts.adapter = postAdapter // Asigna el adaptador al RecyclerView
                Toast.makeText(requireContext(), "drafts cargados", Toast.LENGTH_LONG).show()
            }
        })
    }

}