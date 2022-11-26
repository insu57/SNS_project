package com.example.sns_project

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.item_frienditem.view.*


class searchFragment : Fragment() {

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
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_search,container,false)
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_search,container,false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        var searchingindex = ""

        view.SearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("1")
            }


            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("2")
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { searchuser(it) }
                println(s.toString())
            }

            private fun searchuser(text: Editable) {
                view.searchfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
                view.searchfragment_recyclerview.adapter = SearchUserViewAdapter(text.toString())

            }

        })

        storage = Firebase.storage




        //view.searchfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        //view.searchfragment_recyclerview.adapter = SearchUserViewAdapter()




        // Inflate the layout for this fragment
        return view
    }




    inner class SearchUserViewAdapter(searchuserinfo :String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        var checkuser = FirebaseAuth.getInstance().currentUser?.email // 자신의 이메일 정보
        var userArr : ArrayList<userDTO> = arrayListOf() // 유저 정보 담을 배열
        var userList : ArrayList<String> = arrayListOf() // 유저의 이메일(id값)을 담을 배열

        init{ // 모음
            firestore?.collection("userinfo")?.addSnapshotListener {
                    querySnapshot, firebaseFirestoreException ->
                userArr.clear()
                userList.clear()
                println(searchuserinfo)

                for(snapshot in querySnapshot!!.documents){

                    var item = snapshot.toObject(userDTO::class.java)
                    if (item != null) {
                        if(item.Name?.contains(searchuserinfo) == true && checkuser != snapshot.id){
                            userArr.add(item!!) // 이름 ( doc.name)
                            userList.add(snapshot.id) // id값 ( 이메일 )
                        }
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
            val profileImage = storage.getReferenceFromUrl("gs://sns-project-c4954.appspot.com/image/${userList[position]}/${userList[position]}")

            println(userArr[position])
            viewholder.friendviewitem_profile_textview.text = userArr[position].Name + " " + userList[position]
            // 유저 이름 + 유저 이메일정보 ( 추후에 사진 대신 프로필 사진 구현 예정)
            displayImageRef(profileImage, viewholder.friendviewitem_profile_image)

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

    private fun displayImageRef(imageRef : StorageReference?, view: ImageView){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {

            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener(){
           view.setImageResource(R.drawable.ic_person)
        }
    }
}
