package com.example.lumiere.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.lumiere.SearchResultActivity
import com.example.lumiere.Models.Category
import com.example.lumiere.Models.Post
import com.example.lumiere.RestEngine
import com.example.lumiere.Service
import com.example.lumiere.databinding.FragmentSearchBinding
import com.example.lumiere.responseBody.PostRB
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class SearchFragment : Fragment() {
    var categoryArray: List<Category> = emptyList()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCategories()
        binding.button4.setOnClickListener {
            search()
        }

    }
    fun getCategories(){
        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Category>> = service.getCategories()

        result.enqueue(object : Callback<List<Category>> {
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: " + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                categoryArray = response.body() ?: emptyList()
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categoryArray.map { it.name })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter = adapter
                Toast.makeText(requireContext(), "Categorias cargados", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun search(){
        val selectedPosition = binding.spinner.selectedItemPosition
        val selectedCategoryId = categoryArray.getOrNull(selectedPosition)?.id ?: 0

        val queryPost = Post(title = binding.editTextText4.text.toString(), category_id = selectedCategoryId)

        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<PostRB> = service.advancedSearch(queryPost)

        result.enqueue(object : Callback<PostRB> {
            override fun onFailure(call: Call<PostRB>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: " + t.message, Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<PostRB>, response: Response<PostRB>) {
                val postsRB = response.body()
                if(postsRB?.status == "success"){
                    val posts = postsRB?.list
                    val intent = Intent(requireContext(), SearchResultActivity::class.java)
                    intent.putExtra("posts", ArrayList(posts))
                    intent.putExtra("query", binding.editTextText4.text.toString())
                    intent.putExtra("category", categoryArray.getOrNull(selectedPosition)?.name ?: "")
                    startActivity(intent)

                    Toast.makeText(requireContext(), "Posts obtenidos", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(requireContext(), "No se encontraron posts", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}