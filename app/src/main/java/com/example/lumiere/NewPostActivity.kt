package com.example.lumiere

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.google.gson.Gson
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
    private var draftsArray: MutableList<Post> = mutableListOf()


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
            if (validatePost())
            post(1)
        }


        val post = intent.getParcelableExtra<Post>("PostData")
        if (post != null) {
            Toast.makeText(this, "Post ID recibido: ${post.id}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun validatePost(): Boolean {
        val title = binding.titleETNewPost.text.toString().trim()
        val selectedCategoryId = binding.spinner2.selectedItemPosition
        val isImageSelected = imgArray != null

        // Verifica que el título no esté vacío
        if (title.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un título", Toast.LENGTH_SHORT).show()
            return false
        }

        // Verifica que una categoría haya sido seleccionada
        if (selectedCategoryId == -1 || categoryArray.isEmpty()) {
            Toast.makeText(this, "Por favor, selecciona una categoría", Toast.LENGTH_SHORT).show()
            return false
        }

        // Verifica que se haya seleccionado una imagen
        if (!isImageSelected) {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show()
            return false
        }

        return true // Si todos los campos están completos, devuelve true
    }
    fun post(status:Int){
        // Recuperar el estado de sesión
        val sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false) // false es el valor por defecto
        val userId = sharedPreferences.getInt("userId", 0) // null es el valor por defecto

        if(isInternetAvailable(this)){
            //Si esta conectado a internet hacer el procedimiento normal
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
                    status
                )

                val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
                val result: Call<PostRB> = service.savePost(album)

                result.enqueue(object: Callback<PostRB> {
                    override fun onFailure(call: Call<PostRB>, t: Throwable) {
                        Toast.makeText(this@NewPostActivity,"Error" + t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<PostRB>, response: Response<PostRB>) {
                        val postrb = response.body()
                        //Toast.makeText(this@SaveAlbumActivity,"OK", Toast.LENGTH_LONG).show()
                        if (status == 2) { // Si es un borrador
                            album.id = postrb?.postId ?: 0
                            //fetchDraftsFromDatabase(userId) // Actualiza el array de borradores
                            loadDraftsFromSharedPreferences()
                            draftsArray.add(album)
                            // Guarda los borradores en SharedPreferences
                            val sharedPreferences = getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()

                            // Convierte el array actualizado a JSON
                            val gson = Gson()
                            val jsonDrafts = gson.toJson(draftsArray)

                            // Guarda el JSON en SharedPreferences
                            editor.putString("drafts", jsonDrafts)
                            editor.apply()

                        }
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

        else{

            //agregar el post al array de borradores
            val encodedString: String = Base64.getEncoder().encodeToString(this.imgArray)

            val strEncodeImage:String = "data:image/png;base64," + encodedString

            val selectedPosition = binding.spinner2.selectedItemPosition
            val selectedCategoryId = categoryArray.getOrNull(selectedPosition)?.id ?: 0

            val album = Post(0,
                userId,
                selectedCategoryId,
                strEncodeImage,
                binding.titleETNewPost.text.toString(),
                status
            )
            loadDraftsFromSharedPreferences()
            draftsArray.add(album)
            // Guarda los borradores en SharedPreferences
            val sharedPreferences = getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            // Convierte el array actualizado a JSON
            val gson = Gson()
            val jsonDrafts = gson.toJson(draftsArray)

            // Guarda el JSON en SharedPreferences
            editor.putString("drafts", jsonDrafts)
            editor.apply()

            Toast.makeText(this, "Borrador guardado localmente", Toast.LENGTH_SHORT).show()
            finish() // Vuelve a la actividad anterior

        }

    }

    fun getCategories() {
        if (isInternetAvailable(this)) {
            // Si hay internet, realiza la solicitud a la API
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Category>> = service.getCategories()

            result.enqueue(object : Callback<List<Category>> {
                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    Toast.makeText(this@NewPostActivity, "Error al cargar categorías: ${t.message}", Toast.LENGTH_LONG).show()
                    loadCategoriesFromSharedPreferences() // Carga las categorías locales si falla
                }

                override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                    if (response.isSuccessful) {
                        categoryArray = response.body() ?: emptyList()

                        // Guarda las categorías en SharedPreferences para usarlas offline
                        saveCategoriesToSharedPreferences(categoryArray)

                        // Actualiza el spinner con las categorías
                        updateCategorySpinner()
                        Toast.makeText(this@NewPostActivity, "Categorías cargadas correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@NewPostActivity, "Error al obtener categorías del servidor", Toast.LENGTH_LONG).show()
                        loadCategoriesFromSharedPreferences() // Carga las categorías locales si falla
                    }
                }
            })
        } else {
            // Si no hay internet, carga las categorías desde SharedPreferences
            loadCategoriesFromSharedPreferences()
        }
    }

    override fun onBackPressed() {
        if (validatePost()) {
            // Mostrar diálogo si los campos están llenos
            val builder = android.app.AlertDialog.Builder(this)
            builder.setMessage("¿Quieres guardar esto como borrador?")
                .setPositiveButton("Sí") { dialog, _ ->
                    // Llama a post con estado de borrador
                    post(2)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                    super.onBackPressed()  // Vuelve a la actividad anterior sin guardar
                }
            builder.create().show()
        } else {
            super.onBackPressed() // Si no están completos, vuelve normalmente
        }
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

    fun loadDraftsFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)
        val gson = Gson()
        val jsonDrafts = sharedPreferences.getString("drafts", null)

        if (jsonDrafts != null) {
            val type = object : com.google.gson.reflect.TypeToken<MutableList<Post>>() {}.type
            draftsArray = gson.fromJson(jsonDrafts, type) // Convierte el JSON a un MutableList<Post>
        } else {
            draftsArray = mutableListOf() // Si no hay borradores, inicializa una lista vacía
        }

        Toast.makeText(this, "Borradores cargados localmente", Toast.LENGTH_SHORT).show()
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
    fun saveCategoriesToSharedPreferences(categories: List<Category>) {
        val sharedPreferences = getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val jsonCategories = gson.toJson(categories)
        editor.putString("categories", jsonCategories)
        editor.apply()
    }
    fun loadCategoriesFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)
        val gson = Gson()
        val jsonCategories = sharedPreferences.getString("categories", null)

        if (jsonCategories != null) {
            val type = object : com.google.gson.reflect.TypeToken<List<Category>>() {}.type
            categoryArray = gson.fromJson(jsonCategories, type)
            updateCategorySpinner()
            Toast.makeText(this, "Categorías cargadas localmente", Toast.LENGTH_SHORT).show()
        } else {
            categoryArray = emptyList()
            Toast.makeText(this, "No hay categorías guardadas localmente", Toast.LENGTH_SHORT).show()
        }
    }
    fun updateCategorySpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryArray.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner2.adapter = adapter
    }



}