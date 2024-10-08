package com.example.lumiere

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lumiere.Fragments.DraftsFragment
import com.example.lumiere.Fragments.HomeFragment
import com.example.lumiere.Fragments.MyAccountFragment
import com.example.lumiere.Fragments.SearchFragment
import com.example.lumiere.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val draftsFragment = DraftsFragment()
        val searchFragment = SearchFragment()
        val myAccountFragment = MyAccountFragment()
        // Cargar el fragmento inicial
        makeCurrentFragment(homeFragment)

        // Listener para el BottomNavigationView
        binding.toolbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.home -> {
                    makeCurrentFragment(homeFragment)
                    true
                }

                R.id.search -> {
                    makeCurrentFragment(searchFragment)
                    true
                }

                R.id.account -> {
                    makeCurrentFragment(myAccountFragment)
                    true
                }
                R.id.add -> {
                    val intent = Intent(this, NewPostActivity::class.java)
                    startActivity(intent)
                    false
                }

                R.id.drafts -> {
                    makeCurrentFragment(draftsFragment)
                    true
                }
                else -> false
            }
        }
    }

    // Método para reemplazar el fragmento en el FrameLayout
    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
}