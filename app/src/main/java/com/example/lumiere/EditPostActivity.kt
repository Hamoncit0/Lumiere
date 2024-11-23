package com.example.lumiere

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lumiere.Models.Category
import com.example.lumiere.Models.Post
import com.example.lumiere.Models.User
import com.example.lumiere.databinding.ActivityEditPostBinding
import com.example.lumiere.databinding.ActivityNewPostBinding
import com.example.lumiere.databinding.DialogSuccessBinding
import com.example.lumiere.responseBody.PostRB
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream

class EditPostActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditPostBinding
    var imgArray: MutableList<String> = mutableListOf()
    var categoryArray: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar : Toolbar = binding.toolbarEdit
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
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, EditPostActivity.PICK_IMAGES_REQUEST)
        }


        val post = intent.getParcelableExtra<Post>("PostData")
        post?.let{
            binding.apply {
                titleETEditPost.setText(post.title)
            }

            // Cargar categorías y seleccionar la correspondiente
            getCategories {
                val selectedCategoryPosition = categoryArray.indexOfFirst { category ->
                    category.id == it.category_id
                }

                if (selectedCategoryPosition != -1) {
                    binding.spinner2.setSelection(selectedCategoryPosition)
                } else {
                    Toast.makeText(this, "Categoría no encontrada", Toast.LENGTH_SHORT).show()
                }
            }


            if (post.image?.startsWith("data:image/jpeg;base64,") ?: false) {
                post.image = post.image?.replace("data:image/jpeg;base64,", "")
            }

            var strImage = post.image?.replace("data:image/jpeg;base64,", "")
            strImage = post.image?.replace("data:image/png;base64,", "")
            val bitmaps = mutableListOf<Bitmap>()

            // Primero, si hay una imagen principal (no en array) en el objeto `post`
            if (!strImage.isNullOrEmpty()) {
                try {
                    val byteArray = Base64.decode(strImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    bitmaps.add(bitmap)  // Añadir la imagen principal a la lista
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }

            // Luego, si hay más imágenes en el array `post.images`
            post.images?.forEach { strImage ->
                try {
                    val byteArray = Base64.decode(strImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    bitmaps.add(bitmap)  // Añadir cada imagen al listado
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }

            // Ahora mostrar todas las imágenes en el LinearLayout
            if (bitmaps.isNotEmpty()) {
                displaySelectedImages(bitmaps)
            }

        }

        binding.editPostButton.setOnClickListener {
            if (validatePost())
                updatePost(post?: Post())
        }
    }

    fun updatePost(post: Post) {

        // Convertir el ByteArray a una cadena Base64 en cada uno de los elementos de imgArray
        val encodedImages = imgArray

        //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
        // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
        val selectedPosition = binding.spinner2.selectedItemPosition
        val selectedCategoryId = categoryArray.getOrNull(selectedPosition)?.id ?: 0

        val postToUpdate = Post(post.id,
            post.user_id,
            selectedCategoryId,
            "",
            binding.titleETEditPost.text.toString(),
            post.status,
            "",
            "",
            encodedImages)

        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<PostRB> = service.editPost(postToUpdate)
        result.enqueue(object: Callback<PostRB>{
            override fun onFailure(call: Call<PostRB>, t: Throwable) {
                Toast.makeText(this@EditPostActivity, "Error al editar post: ${t.message}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<PostRB>, response: Response<PostRB>) {
                val postRB = response.body()
                if(postRB?.status == "success"){
                    // Inflar el layout del diálogo usando binding
                    val dialogBinding = DialogSuccessBinding.inflate(layoutInflater)

                    // Crear el AlertDialog con el layout inflado
                    val builder = android.app.AlertDialog.Builder(this@EditPostActivity)
                    builder.setView(dialogBinding.root)

                    val dialog = builder.create()

                    // Establecer el mensaje dinámicamente (puedes cambiarlo según el caso)
                    val successMessage = "Post editado correctamente"  // Cambia este mensaje si es necesario
                    dialogBinding.textView15.text = successMessage

                    // Configurar el botón "OK" para cerrar el diálogo y regresar a la actividad anterior
                    dialogBinding.okBtnDialog.setOnClickListener {
                        dialog.dismiss()
                        finish() // Vuelve a la actividad anterior
                    }

                    // Mostrar el diálogo
                    dialog.show()
                }else{
                    Toast.makeText(this@EditPostActivity, "Error al editar post: ${postRB?.message}", Toast.LENGTH_LONG).show()

                }
            }
        })
    }
    private fun validatePost(): Boolean {
        val title = binding.titleETEditPost.text.toString().trim()
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

    fun getCategories(onCategoriesLoaded: (() -> Unit)? = null) {
        if (isInternetAvailable(this)) {
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<List<Category>> = service.getCategories()

            result.enqueue(object : Callback<List<Category>> {
                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    Toast.makeText(this@EditPostActivity, "Error al cargar categorías: ${t.message}", Toast.LENGTH_LONG).show()
                    loadCategoriesFromSharedPreferences()
                    onCategoriesLoaded?.invoke() // Llamar el callback incluso si falla
                }

                override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                    if (response.isSuccessful) {
                        categoryArray = response.body() ?: emptyList()
                        saveCategoriesToSharedPreferences(categoryArray)
                        updateCategorySpinner()
                        Toast.makeText(this@EditPostActivity, "Categorías cargadas correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@EditPostActivity, "Error al obtener categorías del servidor", Toast.LENGTH_LONG).show()
                        loadCategoriesFromSharedPreferences()
                    }
                    onCategoriesLoaded?.invoke() // Llamar el callback después de cargar
                }
            })
        } else {
            loadCategoriesFromSharedPreferences()
            onCategoriesLoaded?.invoke() // Llamar el callback si no hay internet
        }
    }


    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
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
    fun saveCategoriesToSharedPreferences(categories: List<Category>) {
        val sharedPreferences = getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val jsonCategories = gson.toJson(categories)
        editor.putString("categories", jsonCategories)
        editor.apply()
    }


    // En el método onActivityResult, donde procesas las imágenes seleccionadas:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImages = mutableListOf<Bitmap>()

            // Si hay múltiples imágenes seleccionadas
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                    val photo = BitmapFactory.decodeStream(inputStream)

                    selectedImages.add(photo)
                }
            }
            // Si solo se seleccionó una imagen
            else if (data.data != null) {
                val imageUri = data.data
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                val photo = BitmapFactory.decodeStream(inputStream)

                selectedImages.add(photo)
            }

            // Muestra las imágenes en el layout
            displaySelectedImages(selectedImages)
        }
    }


    // Función para mostrar las imágenes seleccionadas en el LinearLayout
    private fun displaySelectedImages(images: List<Bitmap>) {
        // Limpiar el contenedor de imágenes antes de añadir las nuevas
        binding.imagesContainer.removeAllViews()

        // Limpiar la lista de imágenes base64
        imgArray.clear()

        for (image in images) {
            val imageView = ImageView(this)
            imageView.setImageBitmap(image)

            // Ajustar el tamaño de las imágenes si es necesario
            imageView.layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                marginEnd = 16
            }

            // Añadir el ImageView al contenedor
            binding.imagesContainer.addView(imageView)

            // Convertir el Bitmap a ByteArray y luego a base64
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val byteArray = stream.toByteArray()

            // Convertir el byteArray a base64 con el prefijo adecuado
            val base64Image = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(byteArray)
            imgArray.add(base64Image)  // Guardar la imagen en la lista
        }
    }


    // Constante para identificar la solicitud de selección de imagen
    companion object {
        const val PICK_IMAGES_REQUEST = 2
    }

    private fun cleanBase64Prefix(base64String: String): String {
        return base64String
            .replace("data:image/jpeg;base64,", "")
            .replace("data:image/png;base64,", "")
            .replace("data:image/jpg;base64,", "")
    }

}