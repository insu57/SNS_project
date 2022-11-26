package com.example.sns_project

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.ItemsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

data class Items(
    val id: String, val Name: String, val userMail: String,
    val imagePath: String, val text: String,
    val timestamp: String
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
private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
private val commentsCollectionRef = db.collection("userPost")
private var contentUidList : ArrayList<String> = arrayListOf()


class PostViewHolder(val binding: ItemsBinding) :RecyclerView.ViewHolder(binding.root)


class PostAdapter(private val context: Context, private  var itemList: List<Items>)
    : RecyclerView.Adapter<PostViewHolder>(){

    init{
        commentsCollectionRef.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
            contentUidList.clear()
            if(querySnapshot == null) return@addSnapshotListener

            for(snapshot in querySnapshot!!.documents)
                contentUidList.add(snapshot.id)
            notifyDataSetChanged()
        }
    }

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
        val profileImage = storage.getReferenceFromUrl("gs://sns-project-c4954.appspot.com/image/${item.userMail}/${item.userMail}")

        holder.binding.textMail.text = item.userMail
        holder.binding.textName.text = item.Name
        holder.binding.textView.text = item.text
        holder.binding.commentimage.setImageResource(R.mipmap.ic_comment)
        displayImageRef(imageRef,holder.binding.imagePhoto)
        holder.binding.commentimage.setOnClickListener{ v-> //선택한 게시물의 댓글 보기
            var intent = Intent(v.context, CommentActivity::class.java)
            intent.putExtra("contentUid", contentUidList[position])
            startActivity(v.context, intent, null)
        }


        holder.binding.imageProfile.setOnClickListener{
            val intent = Intent(this.context,  MyProfileActivity::class.java)
            intent.putExtra("contentUid", contentUidList[position])
            intent.putExtra("userMail",item.userMail)
            startActivity(this.context, intent, null)
        }

        displayImageRef(profileImage, holder.binding.imageProfile)




    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    private fun displayImageRef(imageRef : StorageReference?, view: ImageView){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {

            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener(){
            //println("Profile Image Default")
            view.setImageResource(R.drawable.ic_person)
        }
    }


}