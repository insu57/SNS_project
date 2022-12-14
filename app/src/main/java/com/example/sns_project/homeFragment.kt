package com.example.sns_project


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.sns_project.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timer

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

            startActivity(Intent(activity,PostActivity::class.java))//???????????? ??? ???????????? ??????

        }

        //binding.button2.setOnClickListener{ // ???????????? ??? ????????? ??????(?????? ??????)
        //    startActivity(Intent(activity,CommentActivity::class.java))
       // }

        //DB?????? ???????????? ????????????????????? ??????

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = context?.let { PostAdapter(it, emptyList()) }
        binding.recyclerView.adapter =adapter

        binding.button.setOnClickListener {
            updateList()
        }

        updateList()




    }

    override fun onResume() {
            super.onResume()
            Timer().schedule(1000){
                updateList()
            }
        }




    fun updateList(){
        var userArr: ArrayList<userDTO> = arrayListOf() // ????????? ?????????
        var userList: ArrayList<String> = arrayListOf() // ????????? email
        var userArr2: ArrayList<userDTO> = arrayListOf() // ?????????????????? ????????? ?????????
        var userList2: ArrayList<String> = arrayListOf() // ???????????? email
        var allArr: ArrayList<userDTO> = arrayListOf() // ??????
        var allList: ArrayList<String> = arrayListOf() // ??????
        var checkuser = FirebaseAuth.getInstance().currentUser?.email // ?????? ??????
        var currentuser :userDTO? = null // ??????????????? ?????????????????? ????????? ????????????



        userinfoCollectionRef.get().addOnSuccessListener { // ????????????
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
                    if(checkuser==doc.id){ // ??? ?????????????????? ?????? ??????

                    }
                    else if(item.show=="none"){ // ???????????????
                        userArr.add(item!!)
                        userList.add(doc.id)
                    }
                    else if(item.show=="friend"){ // ????????? ????????????
                        var findcount = 0
                        // ????????? ????????????, ???????????? ????????? ???????????? ????????? ????????? ????????? ?????? ???????????? ????????? ?????? ??????
                        for(i in 0 until (item.response?.size!!)){ // response ???????????? ?????? ?????? ?????? ?????????
                            if(checkuser == item?.response?.get(i)){


                                for(j in 0 until (currentuser?.request?.size!!)) {
                                    if(doc.id == currentuser?.request!![j]){
                                        findcount = 1
                                    }
                                }
                            }

                        }

                        if(findcount == 0){

                            userArr2.add(item!!) // ????????? ????????? ????????? ?????? ??????
                            userList2.add(doc.id) // ????????? ???????????? ?????? ??????
                        }



                    }

                } // ?????? ( doc.name)

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