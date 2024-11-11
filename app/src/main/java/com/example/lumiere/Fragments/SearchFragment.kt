package com.example.lumiere.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lumiere.Models.Category
import com.example.lumiere.Models.Post
import com.example.lumiere.PostAdapter
import com.example.lumiere.R
import com.example.lumiere.RestEngine
import com.example.lumiere.Service
import com.example.lumiere.databinding.ActivityNewPostBinding
import com.example.lumiere.databinding.FragmentHomeBinding
import com.example.lumiere.databinding.FragmentMyAccountBinding
import com.example.lumiere.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


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
}