package com.example.sns_project

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.ItemsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

data class Items(
    val id: String, val name: String, val userMail: String,
    val imagePath: String, val text: String,
    val timestamp: String
){

    constructor(doc: QueryDocumentSnapshot):
            this(doc.id,doc["name"].toString(), doc["userMail"].toString(),
                doc["imagePath"].toString(),doc["text"].toString(),
                doc["timestamp"].toString()
            )
    constructor(key: String, map:Map<*, *>):
            this(key,map["name"].toString(), map["userMail"].toString(),
                map["imagePath"].toString(), map["text"].toString(),
                map["timestamp"].toString()
            )
}
lateinit var storage: FirebaseStorage
private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

private val userPostCollectionRef = db.collection("userPost")
private var contentUidList : ArrayList<String> = arrayListOf()


class PostViewHolder(val binding: ItemsBinding) :RecyclerView.ViewHolder(binding.root)


class PostAdapter(private val context: Context, private  var itemList: List<Items>)
    : RecyclerView.Adapter<PostViewHolder>(){

    init{


        userPostCollectionRef.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
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
        val myMail = Firebase.auth.currentUser?.email


        if(item.userMail == myMail){
            holder.binding.buttonDelete.visibility = View.VISIBLE
            println("button  VISIBLE")
        }
        holder.binding.buttonDelete.setOnClickListener {
            userPostCollectionRef.document(item.id).delete().addOnSuccessListener {
                updateList(itemList)
            }
        }


        holder.binding.textMail.text = item.userMail
        holder.binding.textName.text = item.name
        holder.binding.textView.text = item.text
        holder.binding.commentimage.setImageResource(R.mipmap.ic_comment)
        displayImageRef(imageRef,holder.binding.imagePhoto)






        holder.binding.commentimage.setOnClickListener{  //선택한 게시물의 댓글 보기
            val intent = Intent(this.context, CommentActivity::class.java)
            intent.putExtra("contentUid", item.id)
            startActivity(this.context, intent, null)
        }


        holder.binding.imageProfile.setOnClickListener{
            val intent = Intent(this.context,  MyProfileActivity::class.java)
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