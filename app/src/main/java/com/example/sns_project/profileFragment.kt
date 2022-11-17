package com.example.sns_project

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sns_project.databinding.FragmentHomeBinding
import com.example.sns_project.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.item_frienditem.view.*
import java.util.Objects

data class userDTO(var friend : Int? = 0, var Name: String? = null, var request: ArrayList<String>? = null, var requestcount: Int? = 0, var response: ArrayList<String>? = null , var responsecount: Int? = 0, var show: String? = "all" ) // 파이어베이스 데이터베이스에서 불러오는 값들을 저장할 데이터 클래스


class profileFragment : Fragment(R.layout.fragment_profile) {

    var firestore : FirebaseFirestore? = null
    var fragmentView : View? = null
    var uid : String? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val userCollectionRef = db.collection("userinfo")


    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_profile,container,false)
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_profile,container,false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.profilefragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        view.profilefragment_recyclerview.adapter = UserProfileViewAdapter()




        // Inflate the layout for this fragment
        return view
    }



    inner class UserProfileViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var checkuser = FirebaseAuth.getInstance().currentUser?.email // 자신의 이메일 정보
        var userArr : ArrayList<userDTO> = arrayListOf() // 유저 정보 담을 배열
        var userList : ArrayList<String> = arrayListOf() // 유저의 이메일(id값)을 담을 배열

        init{ // 모음
            firestore?.collection("userinfo")?.addSnapshotListener {
                    querySnapshot, firebaseFirestoreException ->
                userArr.clear()
                userList.clear()

                for(snapshot in querySnapshot!!.documents){

                    var item = snapshot.toObject(userDTO::class.java)
                    if(checkuser != snapshot.id){
                        userArr.add(item!!) // 이름 ( doc.name)
                        userList.add(snapshot.id) // id값 ( 이메일 )
                    }
                }
                notifyDataSetChanged() // 갱신
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_frienditem, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewholder = (holder as CustomViewHolder).itemView
            println(userArr[position])
            viewholder.friendviewitem_profile_textview.text = userArr[position].Name + " " + userList[position]
            // 유저 이름 + 유저 이메일정보 ( 추후에 사진 대신 프로필 사진 구현 예정)

            viewholder.friendviewitem_profile_image.setOnClickListener{ v->
                var intent = Intent(v.context, UserActivity::class.java)
                intent.putExtra("currentemail", checkuser)
                intent.putExtra("destinationemail", userList[position])
                println("go")
                startActivity(intent)

            }
        }

        override fun getItemCount(): Int {
            return userArr.size
        }
    }


}

