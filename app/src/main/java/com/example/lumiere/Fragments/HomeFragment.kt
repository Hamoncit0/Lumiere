package com.example.lumiere.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lumiere.Models.Post
import com.example.lumiere.PostAdapter
import com.example.lumiere.RestEngine
import com.example.lumiere.Service
import com.example.lumiere.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    var userId: Int = 0
    // Lista de álbumes y adaptador para el RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val postList: MutableList<Post> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa el RecyclerView y el adaptador
        val recyclerView = binding.recyclerViewHome

        val sharedPreferences = requireContext().getSharedPreferences("USER_PREF", AppCompatActivity.MODE_PRIVATE)
        userId = sharedPreferences.getInt("userId", 0) // null es el valor por defecto

        // Inicializa el adaptador con la lista vacía
        postAdapter = PostAdapter(postList, userId)
        recyclerView.adapter = postAdapter
        // Cargar álbumes desde el servidor
        getPosts()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //OBTENER POSTS
    // Obtener los posts y actualizar la lista existente
    private fun getPosts() {
        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Post>> = service.getPosts()

        result.enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: " + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val posts = response.body() ?: emptyList()
                val filteredPosts = posts.filter { it.status == 1 }

                // Actualiza la lista y notifica al adaptador
                postList.clear() // Limpia la lista existente
                postList.addAll(filteredPosts) // Agrega los nuevos datos
                postAdapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado

                Toast.makeText(requireContext(), "Posts cargados", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getPosts()
    }
}