package com.example.sns_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.ItemCommentBinding
import com.google.firebase.firestore.QueryDocumentSnapshot
import android.content.Context

data class Comment(val id: String, val name: String, val comment: String, val timestamp: Long) {
    constructor(doc: QueryDocumentSnapshot) :
            this(doc.id, doc["name"].toString(), doc["comment"].toString(),
                doc["timestamp"] as Long
            )
    constructor(key: String, map: Map<*, *>) :
            this(key, map["name"].toString(), map["comment"].toString(), map["timestamp"] as Long)
}

class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

class CommentAdapter(private val context: Context, private var comments: List<Comment>)
    : RecyclerView.Adapter<CommentViewHolder>(){

    fun updateComment(newList: List<Comment>){
        comments = newList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemCommentBinding = ItemCommentBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.binding.commentText.text = comment.comment
        holder.binding.commentUserid.text = comment.name
        holder.binding.commentProjile.setImageResource(R.drawable.ic_profile_default);
    }

    override fun getItemCount() = comments.size
}