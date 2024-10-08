package com.example.lumiere.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lumiere.Classes.Post
import com.example.lumiere.PostAdapter
import com.example.lumiere.R
import com.example.lumiere.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val posts = listOf(
            Post("https://i.pinimg.com/236x/d5/39/17/d539174e175e07e8a374616766a44750.jpg", "Hamon", "Calacas"),
            Post("https://i.pinimg.com/236x/4d/ce/f6/4dcef66683e7c0eb7caf8cc59c687845.jpg", "Hamon", "yo ese"),
            Post("https://i.pinimg.com/236x/bc/2c/84/bc2c8473fc5651bb2a6d9ee16f31ea4c.jpg", "Hamon", "yo ese"),
            Post("https://i.pinimg.com/236x/fa/02/c5/fa02c54a71ec17b8a5273ccdb252dc30.jpg", "Hamon", "yo ese")
        )

        val recyclerView = binding.recyclerViewHome
        recyclerView.adapter = PostAdapter(posts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}