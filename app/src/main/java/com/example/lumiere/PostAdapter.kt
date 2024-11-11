package com.example.lumiere

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
                    fetchDraftsFromDatabase( holder)
                    Toast.makeText(holder.itemView.context, "Publicación eliminada", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(holder.itemView.context, "Error al eliminar el post", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun publishPost(postId: Int, position: Int, holder: ViewHolder) {
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
                    // Eliminar el post en la lista visual solo si la API responde correctamente
                    posts.removeAt(position)
                    notifyItemRemoved(position)
                    fetchDraftsFromDatabase(holder)
                    Toast.makeText(holder.itemView.context, "Publicación creada", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(holder.itemView.context, "Error al eliminar el post", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    private fun fetchDraftsFromDatabase(holder: ViewHolder) {
        val userId = currentUser
        val service: Service = RestEngine.getRestEngine().create(Service::class.java)
        val result: Call<List<Post>> = service.getPostsByUserId(userId) // Este endpoint debería devolver todos los borradores del usuario

        result.enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(holder.itemView.context, "Error al obtener borradores: ${t.message}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val drafts = response.body()?: emptyList()

                    var draftsArray: List<Post> = emptyList()
                    draftsArray = drafts.filter { it.status == 2 }

                    val sharedPreferences = holder.itemView.context.getSharedPreferences("LOCAL_STORAGE",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    val editor = sharedPreferences.edit()

                    // Convierte el array a JSON
                    val gson = Gson()
                    val jsonDrafts = gson.toJson(draftsArray) // draftsArray es el ArrayList<Post> de borradores

                    // Guarda el JSON en SharedPreferences
                    editor.putString("drafts", jsonDrafts)
                    editor.apply()

                    Toast.makeText(holder.itemView.context, "Borradores cargados correctamente", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}