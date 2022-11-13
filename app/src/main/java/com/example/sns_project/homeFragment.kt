package com.example.sns_project


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.sns_project.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class homeFragment : Fragment(R.layout.fragment_home) {

    private val db: FirebaseFirestore = Firebase.firestore
    private var adapter : PostAdapter? = null
    private val userinfoCollectionRef = db.collection("userinfo")
    private val userPostCollectionRef = db.collection("userPost")
    private var snapshotListener: ListenerRegistration?= null
    var postMailList: ArrayList<String> = arrayListOf()
    var currentUserUid : String? = null


    private lateinit var binding: FragmentHomeBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val binding = FragmentHomeBinding.bind(view)
        binding = FragmentHomeBinding.bind(view)

        binding.buttonPost.setOnClickListener {
            startActivity(Intent(activity,PostActivity::class.java)) //버튼클릭 시 포스트로 이동
        }

        binding.button2.setOnClickListener{
            startActivity(Intent(activity,CommentActivity::class.java))
        }

        //DB에서 가져와서 리사이클러뷰에 표시

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        //binding.recyclerView.adapter = PostAdapter
        adapter = context?.let { PostAdapter(it, emptyList()) }
        binding.recyclerView.adapter =adapter

        updateList()
    }

    private fun updateList(){
        var userArr: ArrayList<userDTO> = arrayListOf() // 비공개 리스트
        var userList: ArrayList<String> = arrayListOf() // 비공개 email
        var userArr2: ArrayList<userDTO> = arrayListOf() // 친구공개일때 못보는 리스트
        var userList2: ArrayList<String> = arrayListOf() // 친구공개 email
        var allArr: ArrayList<userDTO> = arrayListOf() // 전체
        var allList: ArrayList<String> = arrayListOf() // 전체
        var checkuser = FirebaseAuth.getInstance().currentUser?.email // 현재 유저
        var currentuser :userDTO? = null // 현재유저의 데이터정보를 넣어놀 저장공간



        userinfoCollectionRef.get().addOnSuccessListener { // 전체배열
            for (doc in it) {
                var item = doc.toObject(userDTO::class.java)
                if (item != null) {
                    allArr.add(item!!)
                    allList.add(doc.id)
                    if(checkuser == doc.id){
                        currentuser = item
                        println(currentuser)
                    }
                }
            }
        }


        userinfoCollectionRef.get().addOnSuccessListener {
            for (doc in it) {

                var item = doc.toObject(userDTO::class.java)
                if (item != null) {
                    if(checkuser==doc.id){ // 내 게시물일때는 전부 스킵

                    }
                    else if(item.show=="none"){ // 비공개일때
                        userArr.add(item!!)
                        userList.add(doc.id)
                    }
                    else if(item.show=="friend"){ // 친구만 보기일때
                        var findcount = 0
                        // 서로의 리퀘스트, 리스펀스 배열을 비교해서 서로의 이름이 있으면 친구 하나라도 없으면 친구 아님
                        for(i in 0 until (item.response?.size!!)){ // response 배열안에 있는 값들 전부 끄집기
                            if(checkuser == item?.response?.get(i)){


                                for(j in 0 until (currentuser?.request?.size!!)) {
                                    if(doc.id == currentuser?.request!![j]){
                                        findcount = 1
                                    }
                                }
                            }

                        }

                        if(findcount == 0){

                            userArr2.add(item!!) // 유저의 데이터 정보를 담는 배열
                            userList2.add(doc.id) // 유저의 이메일을 담는 배열
                        }



                    }

                } // 이름 ( doc.name)

            }

        }
        userPostCollectionRef.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener {
            val items = mutableListOf<Items>()
            for(doc in it){
                var count = 0
                for(i in 0 until userList.size!!){
                    if(Items(doc).userMail == userList[i]){
                        count = 1
                    }
                }
                for(i in 0 until userList2.size!!){
                    if(Items(doc).userMail == userList2[i]){
                        count = 1
                    }
                }
                if(count != 1){
                    items.add(Items(doc))
                }

            }
            adapter?.updateList(items)
        }
    }



}