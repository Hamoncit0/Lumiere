package com.example.lumiere

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lumiere.Models.Post
import coil.load
import com.example.lumiere.responseBody.PostRB
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostAdapter (private val posts : MutableList<Post>, private val currentUser: Int ) : RecyclerView.Adapter<PostAdapter.ViewHolder>(){
    class ViewHolder (view : View) : RecyclerView.ViewHolder(view){
        val image : ImageView = view.findViewById(R.id.postImage)
        val title : TextView = view.findViewById(R.id.postDescription)
        val username : TextView = view.findViewById(R.id.postUsername)
        val editButton: ImageButton = view.findViewById(R.id.options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = posts[position]
        holder.title.text = item.title ?: "Título desconocido"
        holder.username.text = item.username ?: "Usuario desconocido"
        // Mostrar botón solo si el usuario es el mismo
        if (item.user_id == currentUser) {
            holder.editButton.visibility = View.VISIBLE
        } else {
            holder.editButton.visibility = View.GONE
        }
        // Decodifica la imagen desde base64
        val strImage = item.image?.replace("data:image/png;base64,", "")
        if (strImage != null) {
            try {
                val byteArray = Base64.decode(strImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                holder.image.setImageBitmap(bitmap)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        // Configura el clic en el botón de edición
        holder.editButton.setOnClickListener {
            // Crea y muestra el diálogo personalizado
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_confirm_delete, null)
            val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.cancelBtnDialog).setOnClickListener {
                dialogBuilder.dismiss() // Cierra el diálogo sin hacer nada
            }

            dialogView.findViewById<Button>(R.id.okBtnDialog).setOnClickListener {
                // Confirmación de eliminación
                deletePost(item.id ?: 0, position, holder)
                dialogBuilder.dismiss() // Cierra el diálogo
            }

            dialogBuilder.show()
        }
    }

    // Función deletePost para eliminar en el backend y actualizar la lista visualmente
    fun deletePost(postId: Int, position: Int, holder: ViewHolder) {
        val postToDelete = Post(id = postId, status = 3)
        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<PostRB> = service.updatePostStatus(postToDelete)

        result.enqueue(object : Callback<PostRB> {
            override fun onFailure(call: Call<PostRB>, t: Throwable) {
                // Error en la conexión o en la llamada
                Toast.makeText(holder.itemView.context, "Error: " + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<PostRB>, response: Response<PostRB>) {
                if (response.isSuccessful) {
                    // Eliminar el post en la lista visual solo si la API responde correctamente
                    posts.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(holder.itemView.context, "Publicación eliminada", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(holder.itemView.context, "Error al eliminar el post", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}