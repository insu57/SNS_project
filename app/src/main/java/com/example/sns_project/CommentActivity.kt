package com.example.sns_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sns_project.databinding.ActivityCommentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCommentBinding
    private var adapter: CommentAdapter? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val userMail = FirebaseAuth.getInstance().currentUser?.email
    private var snapshotListener: ListenerRegistration? = null
    private var contentUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contentUid = intent.getStringExtra("contentUid")
        binding.recyclerViewComment.layoutManager = LinearLayoutManager(this)
        adapter = CommentAdapter(this, emptyList())

        binding.recyclerViewComment.adapter = adapter

        updateComment()
        binding.buttonSend.setOnClickListener{
            addComment()
            binding.editTextComment.setText("")
        }

    }

    override fun onStop() {
        super.onStop()
        snapshotListener?.remove()
    }


    private fun updateComment(){
        db.collection("userPost").document(contentUid!!).collection("comments").orderBy("timestamp").get().addOnSuccessListener {
            val comments = mutableListOf<Comment>()
            for(doc in it){
                comments.add(Comment(doc))
            }
            adapter?.updateComment(comments)
        }
    }

    private fun addComment(){
        val name = userMail
        val comment = binding.editTextComment.text.toString()
        val timestamp = System.currentTimeMillis()

        val commentMap = hashMapOf(
            "name" to name,
            "comment" to comment,
            "timestamp" to timestamp
        )
        db.collection("userPost").document(contentUid!!).collection("comments").add(commentMap)
            .addOnSuccessListener { updateComment() }.addOnFailureListener{}
    }



}

