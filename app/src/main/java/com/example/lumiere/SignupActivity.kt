package com.example.lumiere

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lumiere.Models.User
import com.example.lumiere.databinding.ActivitySignupBinding
import com.example.lumiere.databinding.DialogSuccessBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding // Cambia a lateinit para inicializar luego

    var imgArray:ByteArray? =  null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button5.setOnClickListener { validateAndSignUp()}
    }
    private fun validateAndSignUp() {
        val firstName = binding.firstNameSU.text.toString().trim()
        val lastName = binding.lastNameSU.text.toString().trim()
        val username = binding.usernameSU.text.toString().trim()
        val email = binding.emailSU.text.toString().trim()
        val password = binding.passwordSU.text.toString().trim()
        if (firstName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un nombre.", Toast.LENGTH_SHORT).show()
            return
        }

        if (lastName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un apellido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un nombre de usuario.", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese un correo.", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese una contraseña.", Toast.LENGTH_SHORT).show()
            return
        }
        signUp()

    }
    private fun signUp(){
        //SE CONSTRUYE EL OBJECTO A ENVIAR,  ESTO DEPENDE DE COMO CONSTRUYAS EL SERVICIO
        // SI TU SERVICIO POST REQUIERE DOS PARAMETROS HACER UN OBJECTO CON ESOS DOS PARAMETROS
            val user =   User(0,
            binding.firstNameSU.text.toString(),
            binding.lastNameSU.text.toString(),
            binding.usernameSU.text.toString(),
            binding.emailSU.text.toString(),
            binding.passwordSU.text.toString(),
            null,
            "",
            null)

        val service: Service =  RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<Int> = service.signUp(user)

        result.enqueue(object: Callback<Int> {
            override fun onFailure(call: Call<Int>, t: Throwable) {
                Toast.makeText(this@SignupActivity,"Error" + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                //Toast.makeText(this@SignupActivity,"OK", Toast.LENGTH_LONG).show()
                // Limpiar los campos después de guardar
                binding.firstNameSU.text.clear()
                binding.lastNameSU.text.clear()
                binding.usernameSU.text.clear()
                binding.emailSU.text.clear()
                binding.passwordSU.text.clear()

                // Inflar el layout del diálogo usando binding
                val dialogBinding = DialogSuccessBinding.inflate(layoutInflater)

                // Crear el AlertDialog con el layout inflado
                val builder = android.app.AlertDialog.Builder(this@SignupActivity)
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