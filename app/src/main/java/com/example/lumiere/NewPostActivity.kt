package com.example.lumiere

import android.R
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.lumiere.Models.Category
import com.example.lumiere.Models.Post
import com.example.lumiere.Models.User
import com.example.lumiere.databinding.ActivityNewPostBinding
import com.example.lumiere.databinding.DialogSuccessBinding
import com.example.lumiere.responseBody.PostRB
import com.example.lumiere.responseBody.UserRB
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Base64

class NewPostActivity : AppCompatActivity() {
    lateinit var binding: ActivityNewPostBinding
    var imgArray:ByteArray? =  null
    var categoryArray: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar : Toolbar = binding.toolbar2
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // Vuelve a la actividad anterior
        }
        getCategories()

        // Configurar el botón de selección de imagen
        binding.addPictureBtnNP.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        binding.savePostBtn.setOnClickListener {
            post()
        }
    }

    fun post(){
        // Recuperar el estado de sesión
        val sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false) // false es el valor por defecto
        val userId = sharedPreferences.getInt("userId", 0) // null es el valor por defecto

        if (isLoggedIn && userId != 0) {

            val encodedString: String = Base64.getEncoder().encodeToString(this.imgArray)

            val strEncodeImage:String = "data:image/png;base64," + encodedString

            val selectedPosition = binding.spinner2.selectedItemPosition
            val selectedCategoryId = categoryArray.getOrNull(selectedPosition)?.id ?: 0
            //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
            // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
            val album = Post(0,
                userId,
                selectedCategoryId,
                strEncodeImage,
                binding.titleETNewPost.text.toString(),
                1
                )

            val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<PostRB> = service.savePost(album)

            result.enqueue(object: Callback<PostRB> {
                override fun onFailure(call: Call<PostRB>, t: Throwable) {
                    Toast.makeText(this@NewPostActivity,"Error" + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PostRB>, response: Response<PostRB>) {
                    //Toast.makeText(this@SaveAlbumActivity,"OK", Toast.LENGTH_LONG).show()

                    // Limpiar los campos después de guardar
                    binding.titleETNewPost.text.clear()
                    binding.imageViewNewPost.setImageResource(R.drawable.ic_menu_camera)  // Reemplaza con una imagen de placeholder
                    imgArray = null

                    // Inflar el layout del diálogo usando binding
                    val dialogBinding = DialogSuccessBinding.inflate(layoutInflater)

                    // Crear el AlertDialog con el layout inflado
                    val builder = android.app.AlertDialog.Builder(this@NewPostActivity)
                    builder.setView(dialogBinding.root)

                    val dialog = builder.create()

                    // Configurar el botón "OK" para cerrar el diálogo y regresar a la actividad anterior
                    dialogBinding.okBtnDialog.setOnClickListener {
                        dialog.dismiss()
                        finish() // Vuelve a la actividad anterior
                    }

                    // Mostrar el diálogo
                    dialog.show()
                }
            })
        }
    }

    fun getCategories(){
        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Category>> = service.getCategories()

        result.enqueue(object : Callback<List<Category>> {
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@NewPostActivity, "Error: " + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                categoryArray = response.body() ?: emptyList()
                val adapter = ArrayAdapter(
                    this@NewPostActivity,
                    R.layout.simple_spinner_item,
                    categoryArray.map { it.name })
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spinner2.adapter = adapter
                Toast.makeText(this@NewPostActivity, "Categorias cargados", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Si el usuario seleccionó una imagen, actualizar la vista y guardar el ByteArray
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Carga la imagen seleccionada en el ImageView
            val imageUri = data.data
            binding.imageViewNewPost.setImageURI(imageUri)

            // Cargar el bitmap desde el URI
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
            val photo = BitmapFactory.decodeStream(inputStream)
            val stream = ByteArrayOutputStream()

            // Comprimir el bitmap
            photo.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            imgArray = stream.toByteArray()
        }
    }

    // Constante para identificar la solicitud de selección de imagen
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }
}