package com.example.sns_project


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sns_project.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class homeFragment : Fragment(R.layout.fragment_home) {

    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter : PostAdapter? = null
    private val userinfoCollectionRef = db.collection("userinfo")
    private val userPostCollectionRef = db.collection("userPost")
    private var snapshotListener: ListenerRegistration?= null
    var postMailList: ArrayList<String> = arrayListOf()

    private lateinit var binding: FragmentHomeBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val binding = FragmentHomeBinding.bind(view)
        binding = FragmentHomeBinding.bind(view)

        binding.buttonPost.setOnClickListener {
            startActivity(Intent(activity,PostActivity::class.java)) //버튼클릭 시 포스트로 이동
        }
        //DB에서 가져와서 리사이클러뷰에 표시

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        //binding.recyclerView.adapter = PostAdapter
        adapter = context?.let { PostAdapter(it, emptyList()) }
        binding.recyclerView.adapter =adapter

        updateList()
    }

    private fun updateList(){
        userPostCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener {
            val items = mutableListOf<Items>()
            for(doc in it){
                items.add(Items(doc))
            }
            adapter?.updateList(items)
         }
    }



}