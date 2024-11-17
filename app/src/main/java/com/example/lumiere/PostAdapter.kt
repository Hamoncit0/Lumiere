package com.example.lumiere

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.lumiere.Models.Post
import com.example.lumiere.responseBody.PostRB
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostAdapter (private val posts : MutableList<Post>, private val currentUser: Int ) : RecyclerView.Adapter<PostAdapter.ViewHolder>(){
    class ViewHolder (view : View) : RecyclerView.ViewHolder(view){
        val image : ImageView = view.findViewById(R.id.postImage)
        val title : TextView = view.findViewById(R.id.postDescription)
        val username : TextView = view.findViewById(R.id.postUsername)
        val deleteButton: Button = view.findViewById(R.id.options)
        val postButton: Button = view.findViewById(R.id.add)
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
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.deleteButton.visibility = View.GONE
        }
        if(item.status == 2){
            holder.postButton.visibility = View.VISIBLE
        }else{
            holder.postButton.visibility = View.GONE
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
        holder.deleteButton.setOnClickListener {
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

        holder.postButton.setOnClickListener {
            // Crea y muestra el diálogo personalizado
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_confirm_delete, null)
            val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.textView15).text = "Are you sure you want to publish this post?"
            dialogView.findViewById<TextView>(R.id.textView14).text = "Post?"
            dialogView.findViewById<ImageView>(R.id.imageView9).setImageResource(R.drawable.add)

            dialogView.findViewById<Button>(R.id.cancelBtnDialog).setOnClickListener {
                dialogBuilder.dismiss() // Cierra el diálogo sin hacer nada
            }

            dialogView.findViewById<Button>(R.id.okBtnDialog).setOnClickListener {
                // Confirmación de eliminación
                publishPost(item.id ?: 0, position, holder)
                dialogBuilder.dismiss() // Cierra el diálogo
            }

            dialogBuilder.show()
        }
    }

    // Función deletePost para eliminar en el backend y actualizar la lista visualmente
    fun deletePost(postId: Int, position: Int, holder: ViewHolder) {
        if(postId != 0){
            //si el post existe su id sera diferente de 0 entonces nada mas le cambiara el status
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
                        // Eliminar el post en la lista visual y de los borradores locales solo si la API responde correctamente
                        if(posts[position].status == 2){
                            removeDraftFromArray(position, holder)
                        }else{
                            posts.removeAt(position)
                            notifyItemRemoved(position)
                        }
                        Toast.makeText(holder.itemView.context, "Borrador eliminado", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Error al eliminar el post", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
        else{
            // Eliminar del array de borradores
            removeDraftFromArray(position, holder)
            Toast.makeText(holder.itemView.context, "Borrador eliminado", Toast.LENGTH_LONG).show()
        }

    }

    fun publishPost(postId: Int, position: Int, holder: ViewHolder) {

        if(postId != 0){
            //si el post existe su id sera diferente de 0 entonces nada mas le cambiara el status
            val postToCreate = Post(id = postId, status = 1)
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<PostRB> = service.updatePostStatus(postToCreate)

            result.enqueue(object : Callback<PostRB> {
                override fun onFailure(call: Call<PostRB>, t: Throwable) {
                    // Error en la conexión o en la llamada
                    Toast.makeText(holder.itemView.context, "Error: " + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PostRB>, response: Response<PostRB>) {
                    if (response.isSuccessful) {
                        if(posts[position].status == 2){
                            removeDraftFromArray(position, holder)
                        }
                        Toast.makeText(holder.itemView.context, "Publicación creada", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Error al crear el post", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
        else{
            //si el post no existe su id sera 0 entonces lo guardamos en la base de datos
            val postToCreate = posts[position]
            postToCreate.status = 1
            val service: Service = RestEngine.getRestEngine().create(Service::class.java)
            val result: Call<PostRB> = service.savePost(postToCreate)

            result.enqueue(object : Callback<PostRB> {
                override fun onFailure(call: Call<PostRB>, t: Throwable) {
                    // Error en la conexión o en la llamada
                    Toast.makeText(holder.itemView.context, "Error: " + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PostRB>, response: Response<PostRB>) {
                    if (response.isSuccessful) {
                        // Eliminar el post en la lista visual solo si la API responde correctamente
                        removeDraftFromArray(position, holder)
                        Toast.makeText(holder.itemView.context, "Publicación creada", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(holder.itemView.context, "Error al eliminar el post", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }

    }

    private fun removeDraftFromArray(position: Int, holder: ViewHolder) {
        // Eliminar del array de borradores
        (posts as MutableList).removeAt(position)
        notifyItemRemoved(position) // Actualiza la lista visual

        // Guardar el array actualizado en SharedPreferences
        saveDraftsToSharedPreferences(holder)
    }
    fun saveDraftsToSharedPreferences(holder: ViewHolder) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("LOCAL_STORAGE", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val jsonDrafts = gson.toJson(posts) // Convierte el array a JSON
        editor.putString("drafts", jsonDrafts)
        editor.apply()
    }


}