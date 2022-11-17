package com.example.sns_project

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.ItemsBinding

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

data class Items(val id: String,val Name: String, val userMail: String,
                 val imagePath: String, val text: String,
                 val timestamp : String
                 ){

    constructor(doc: QueryDocumentSnapshot):
            this(doc.id,doc["name"].toString(), doc["userMail"].toString(),
                doc["imagePath"].toString(),doc["text"].toString(),
                doc["timestamp"].toString()
            )
    constructor(key: String, map:Map<*, *>):
            this(key,map["Name"].toString(), map["userMail"].toString(),
                map["imagePath"].toString(), map["text"].toString(),
                map["timestamp"].toString()
            )
}
lateinit var storage: FirebaseStorage

class PostViewHolder(val binding: ItemsBinding) :RecyclerView.ViewHolder(binding.root)


class PostAdapter(private val context: Context, private  var itemList: List<Items>)
    : RecyclerView.Adapter<PostViewHolder>(){




        fun updateList(newList:List<Items>){
            itemList = newList
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding : ItemsBinding = ItemsBinding.inflate(inflater, parent, false)

        storage = Firebase.storage

        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = itemList[position]
        val storageReference = storage.reference
        val imageRef = storage.getReferenceFromUrl(item.imagePath)


        holder.binding.textMail.text = item.userMail
        holder.binding.textName.text = item.Name
        holder.binding.textView.text = item.text
        displayImageRef(imageRef,holder.binding.imagePhoto)


    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    private fun displayImageRef(imageRef : StorageReference?, view: ImageView){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
            view.setImageBitmap(bmp)
        }
    }


}