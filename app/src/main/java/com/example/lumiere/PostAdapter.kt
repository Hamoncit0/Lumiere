package com.example.lumiere

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lumiere.Models.Post
import coil.load

class PostAdapter (private val posts : List<Post>, private val currentUser: Int ) : RecyclerView.Adapter<PostAdapter.ViewHolder>(){
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
    }
}