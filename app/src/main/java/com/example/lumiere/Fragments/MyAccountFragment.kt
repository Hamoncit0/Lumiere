package com.example.lumiere.Fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lumiere.Models.Post
import com.example.lumiere.EditAccountActivity
import com.example.lumiere.HomeActivity
import com.example.lumiere.LoginActivity
import com.example.lumiere.MainActivity
import com.example.lumiere.PostAdapter
import com.example.lumiere.databinding.FragmentMyAccountBinding

class MyAccountFragment : Fragment() {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logOutBtn.setOnClickListener {
            logOut()
        }
        val editAccountButton = binding.imageButton
        editAccountButton.setOnClickListener {
            val intent = Intent(requireContext(), EditAccountActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun logOut() {
        val sharedPreferences = requireContext().getSharedPreferences("USER_PREF", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Limpia todos los datos de sesión
        editor.apply()

        // Redirige al usuario a la pantalla de inicio de sesión
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }


}