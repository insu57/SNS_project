package com.example.sns_project

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.FragmentHomeBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_frienditem.view.*
import kotlinx.android.synthetic.main.item_post.view.*

data class postDTO(var imagePath : String? = null, var text: String? = null, var timestamp: String? = null, var userMail: String? = null) // 파이어베이스 데이터베이스에서 불러오는 값들을 저장할 데이터 클래스

class homeFragment : Fragment(R.layout.fragment_home) {
    lateinit var storage: FirebaseStorage
    var firestore: FirebaseFirestore? = null
    var fragmentView: View? = null
    var uid: String? = null
    private val db: FirebaseFirestore = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storage = Firebase.storage

        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.homerecyclerView.layoutManager = LinearLayoutManager(activity)
        view.homerecyclerView.adapter = PostViewAdapter()

        //DB에서 가져와서 리사이클러뷰에 표시
        view.buttonPost.setOnClickListener {
            startActivity(Intent(activity, PostActivity::class.java)) //버튼클릭 시 포스트로 이동
        }

        view.button2.setOnClickListener {
            startActivity(Intent(activity, CommentActivity::class.java))
        }
        return view
    }



    inner class PostViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var checkuser = FirebaseAuth.getInstance().currentUser?.email // 자신의 이메일 정보
        var postArr: ArrayList<postDTO> = arrayListOf() // 유저 정보 담을 배열
        var userArr: ArrayList<userDTO> = arrayListOf() // 유저의 이메일(id값)을 담을 배열
        var userList: ArrayList<String> = arrayListOf() // 유저의 이메일(id값)을 담을 배열

        init { // 모음
            firestore?.collection("userPost")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    postArr.clear()


                    for (snapshot in querySnapshot!!.documents) {

                        var item = snapshot.toObject(postDTO::class.java)

                        postArr.add(item!!) // 이름 ( doc.name)


                    }
                    notifyDataSetChanged() // 갱신
                }
            firestore?.collection("userinfo")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    userArr.clear()


                    for (snapshot in querySnapshot!!.documents) {

                        var item = snapshot.toObject(userDTO::class.java)

                        userArr.add(item!!) // 이름 ( doc.name)
                        userList.add(snapshot.id)


                    }
                    notifyDataSetChanged() // 갱신
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
            return CustomViewHolder(view)
        }

       inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewholder = (holder as homeFragment.PostViewAdapter.CustomViewHolder).itemView
            println("ok" + postArr[position].userMail)
            println("ok" + postArr[position])
            println("ok2" + userArr.size)
            var check = 0

                for(i in 0 until userArr.size){
                    if(postArr[position].userMail == userList[i]){
                        check = i
                    }
                }

            storage = Firebase.storage
            viewholder.postviewitem_profile_textview.text = userArr[check].Name + " " + postArr[position].userMail
            // 유저 이름 + 유저 이메일정보 ( 추후에 사진 대신 프로필 사진 구현 예정)
            val imageRef = postArr[position].imagePath?.let { storage.getReferenceFromUrl(it) }
            //Glide.with(holder.itemView.context).load(postArr[position].imagePath).into(viewholder.postviewitem_imageview_content)
            displayImageRef(imageRef, viewholder.postviewitem_imageview_content)

            viewholder.postviewitem_text_textview.text = postArr[position].text

            viewholder.postviewitem_profile_image.setOnClickListener{ v->
                var intent = Intent(v.context, UserActivity::class.java)
                intent.putExtra("currentemail", checkuser)
                intent.putExtra("destinationemail", postArr[position].userMail)
                println("go")
                startActivity(intent)

            }
        }

        override fun getItemCount(): Int {
            return postArr.size

        }
        private fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
            imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                view.setImageBitmap(bmp)
            }?.addOnFailureListener {
                // Failed to download the image
            }
        }


    }
}