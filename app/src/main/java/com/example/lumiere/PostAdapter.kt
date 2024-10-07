package com.example.lumiere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lumiere.Classes.Post
import coil.load

class PostAdapter (private val posts : List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>(){
    class ViewHolder (view : View) : RecyclerView.ViewHolder(view){
        val image : ImageView = view.findViewById(R.id.postImage)
        val username : TextView = view.findViewById(R.id.postUsername)
        val description : TextView = view.findViewById(R.id.postDescription)

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
        holder.image.load(data = item.imageURL)
        holder.username.text = item.username
        holder.description.text = item.description
    }
}