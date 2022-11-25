package com.example.sns_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.ItemCommentBinding
import com.google.firebase.firestore.QueryDocumentSnapshot
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date

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

        storage = Firebase.storage
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        val profileImage = storage.getReferenceFromUrl("gs://sns-project-c4954.appspot.com/image/${comment.name}/${comment.name}")

        holder.binding.commentText.text = comment.comment
        holder.binding.commentUserid.text = comment.name
        //holder.binding.commentProjile.setImageResource(R.mipmap.ic_launcher);
        displayImageRef(profileImage, holder.binding.commentProjile)
        holder.binding.commentTime.text = SimpleDateFormat("yyyy-MM-dd hh:mm").format(Date(comment.timestamp)).toString();
    }

    override fun getItemCount() = comments.size


    private fun displayImageRef(imageRef : StorageReference?, view: ImageView){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {

            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener(){
            view.setImageResource(R.drawable.ic_person)
        }
    }
}
