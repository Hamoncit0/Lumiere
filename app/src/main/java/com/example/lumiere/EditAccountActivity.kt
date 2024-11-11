package com.example.lumiere

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.lumiere.Models.User
import com.example.lumiere.databinding.ActivityEditAccountBinding
import com.example.lumiere.databinding.DialogSuccessBinding
import com.example.lumiere.responseBody.UserRB
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream

class EditAccountActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditAccountBinding
    var imgArray:ByteArray? =  null
    var userId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar : Toolbar = binding.editAccountToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // Vuelve a la actividad anterior
        }
        val user = intent.getParcelableExtra<User>("UserData")

        user?.let {
            binding.apply {
                editTextUserName.setText(it.username)
                editTextFirstName.setText(it.first_name)
                editTextLastName.setText(it.last_name)
                editTextPassword3.setText(it.password)
            }
            // Decodifica la imagen desde base64
            val strImage = it.profile_picture?.replace("data:image/png;base64,", "")
            if (!strImage.isNullOrEmpty()) {
                try {
                    val byteArray = Base64.decode(strImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    binding.imageView7.setImageBitmap(bitmap)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }

        binding.button6.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, NewPostActivity.PICK_IMAGE_REQUEST)
        }

        binding.button4.setOnClickListener {
            editAccount(user?.id!!)
        }
    }

    fun editAccount(userId:Int){

        val encodedString: String = if (imgArray != null) {
            java.util.Base64.getEncoder().encodeToString(this.imgArray)
        } else {
            ""
        }

        val strEncodeImage:String = "data:image/png;base64," + encodedString
        //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
        // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
        val userToUpdate =   User(userId,
            binding.editTextFirstName.text.toString(),
            binding.editTextLastName.text.toString(),
            binding.editTextUserName.text.toString(),
            "",
            binding.editTextPassword3.text.toString(),
            null,
            strEncodeImage,
            null)

        val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<UserRB> = service.updateUser(userToUpdate)

        result.enqueue(object: Callback<UserRB> {
            override fun onFailure(call: Call<UserRB>, t: Throwable) {
                Toast.makeText(this@EditAccountActivity,"Error" + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<UserRB>, response: Response<UserRB>) {
                //Toast.makeText(this@SignupActivity,"OK", Toast.LENGTH_LONG).show()

                // Inflar el layout del diálogo usando binding
                val dialogBinding = DialogSuccessBinding.inflate(layoutInflater)

                // Crear el AlertDialog con el layout inflado
                val builder = android.app.AlertDialog.Builder(this@EditAccountActivity)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Si el usuario seleccionó una imagen, actualizar la vista y guardar el ByteArray
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Carga la imagen seleccionada en el ImageView
            val imageUri = data.data
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)

            // Decodificar el bitmap desde el InputStream
            val photo = BitmapFactory.decodeStream(inputStream)

            // Convierte el Bitmap a circular
            val circularBitmap = getCircularBitmap(photo)

            // Establece la imagen circular en el ImageView
            binding.imageView7.setImageBitmap(circularBitmap)

            // Comprimir el bitmap para guardarlo como ByteArray
            val stream = ByteArrayOutputStream()
            circularBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            imgArray = stream.toByteArray()
        }
    }


    // Constante para identificar la solicitud de selección de imagen
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        val squareBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)
        val paint = Paint()
        val shader = BitmapShader(squareBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        paint.shader = shader

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        return output
    }

}