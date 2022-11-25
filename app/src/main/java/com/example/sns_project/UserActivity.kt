package com.example.sns_project

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.sns_project.databinding.ActivityUserBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class UserActivity  : AppCompatActivity() {
    var currentemail : String? = null
    var destionationemail : String? = null
    var firestore : FirebaseFirestore? = null
    private val binding by lazy { ActivityUserBinding.inflate(layoutInflater)}
    var currentuser : userDTO? = null // 유저 정보 담을 배열

    var destionationuser : userDTO? = null // 유저 정보 담을 배열




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("push")
        setContentView(binding.root)
        currentemail = intent.getStringExtra("currentemail")
        destionationemail = intent.getStringExtra("destinationemail")

        storage = Firebase.storage




        val profileImage = storage.getReferenceFromUrl("gs://sns-project-c4954.appspot.com/image/${destionationemail}/${destionationemail}")
        displayImageRef(profileImage, binding.accountIvProfile)


        firestore = FirebaseFirestore.getInstance()

        firestore?.collection("userinfo")
        ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->


            for (snapshot in querySnapshot!!.documents) {

                var item = snapshot.toObject(userDTO::class.java)
                if (currentemail == snapshot.id) {
                    currentuser = item!!
                } else if (destionationemail == snapshot.id) {
                    destionationuser = item!!
                }
            }

            val size = currentuser?.requestcount
            val size2 = destionationuser?.requestcount
            val size3 = currentuser?.responsecount
            val size4 = destionationuser?.responsecount
            var count = 0
            var count2 = 0
            if(size != 0){
                for(i in 0 until size!!){
                    println(destionationemail)
                    var yes : String? = currentuser?.request?.get(i)
                    println(yes)
                    if(currentuser?.request?.get(i) == destionationemail){
                        count = 1

                    }
                }

            }




            if(count == 0){ // 새로 친구 신청
                if(size2 != 0){
                    for(i in 0 until size2!!){

                        if(destionationuser?.request?.get(i) == currentemail){
                            count2 = 1 // 상대는 친구신청을 보내놓은 상태일때

                        }
                    }

                }
                if(count2 == 1){
                    binding.button3.text = getString(R.string.request) // 친구 수락

                }
                else{
                    binding.button3.text = getString(R.string.add) // 친구 요청

                }



            }
            else{
                if(size2 != 0){
                    for(i in 0 until size2!!){

                        if(destionationuser?.request?.get(i) == currentemail){
                            count2 = 1 // 이미 친구인 상태일때

                        }
                    }

                }
                if(count2 == 1){ // 둘다 삭제
                    binding.button3.text = getString(R.string.delete) // 친구 삭제

                }
                else{//사실 혼자만 보내놓은 상태였을때 친구요청 취소

                    binding.button3.text = getString(R.string.cancel) // 친구 요청 취소


                }
            }


        }

        println(currentuser?.request)
        println(currentuser?.response)




        binding.button3.setOnClickListener {
            val size = currentuser?.requestcount
            val size2 = destionationuser?.requestcount
            val size3 = currentuser?.responsecount
            val size4 = destionationuser?.responsecount
            var count = 0
            var count2 = 0
            var idx = 0
            var idx2 = 0
            var idx3 = 0
            var idx4 = 0

            if(size != 0){
                for(i in 0 until size!!){
                    println(destionationemail)
                    var yes : String? = currentuser?.request?.get(i)
                    println(yes)
                    if(currentuser?.request?.get(i) == destionationemail){
                        count = 1
                        idx = i
                    }
                }

            }
            if(size2 != 0) {
                for (i in 0 until size2!!) {

                    if (destionationuser?.request?.get(i) == currentemail) {
                        idx2 = i
                    }
                }
            }
            if(size3 != 0) {
                for (i in 0 until size3!!) {

                    if (currentuser?.response?.get(i) == destionationemail) {
                        idx3 = i
                    }
                }
            }
            if(size4 != 0) {
                for (i in 0 until size4!!) {

                    if (destionationuser?.response?.get(i) == currentemail) {
                        idx4 = i
                    }
                }
            }

            println(count)

            if(count == 0){ // 새로 친구 신청
                if(size2 != 0){
                    for(i in 0 until size2!!){

                        if(destionationuser?.request?.get(i) == currentemail){
                            count2 = 1 // 상대는 친구신청을 보내놓은 상태일때

                        }
                    }

                }
                if(count2 == 1){
                    binding.button3.text = getString(R.string.request) // 친구 수락
                    destionationemail?.let { it1 -> currentuser?.request?.add(it1) }
                    currentuser?.requestcount = currentuser?.requestcount?.plus(1);
                    currentemail?.let { it1 -> destionationuser?.response?.add(it1) }
                    destionationuser?.responsecount = destionationuser?.responsecount?.plus(1);
                    currentuser?.friend = currentuser?.friend?.plus(1);
                    destionationuser?.friend = destionationuser?.friend?.plus(1);
                }
                else{
                    binding.button3.text = getString(R.string.add) // 친구 요청
                    destionationemail?.let { it1 -> currentuser?.request?.add(it1) }
                    currentuser?.requestcount = currentuser?.requestcount?.plus(1);
                    currentemail?.let { it1 -> destionationuser?.response?.add(it1) }
                    destionationuser?.responsecount = destionationuser?.responsecount?.plus(1);
                }



            }
            else{
                if(size2 != 0){
                    for(i in 0 until size2!!){

                        if(destionationuser?.request?.get(i) == currentemail){
                            count2 = 1 // 이미 친구인 상태일때

                        }
                    }

                }
                if(count2 == 1){ // 둘다 삭제
                    binding.button3.text = getString(R.string.delete) // 친구 삭제
                    destionationuser?.response?.removeAt(idx4)
                    currentuser?.response?.removeAt(idx3)
                    destionationuser?.request?.removeAt(idx2)
                    currentuser?.request?.removeAt(idx)
                    destionationuser?.responsecount = destionationuser?.responsecount?.minus(1)
                    currentuser?.responsecount = currentuser?.responsecount?.minus(1)
                    destionationuser?.requestcount = destionationuser?.requestcount?.minus(1)
                    currentuser?.requestcount = currentuser?.requestcount?.minus(1)
                    currentuser?.friend = currentuser?.friend?.minus(1);
                    destionationuser?.friend = destionationuser?.friend?.minus(1);
                }
                else{//사실 혼자만 보내놓은 상태였을때 친구요청 취소

                    binding.button3.text = getString(R.string.cancel) // 친구 요청 취소
                    currentuser?.request?.removeAt(idx)
                    currentuser?.requestcount = currentuser?.requestcount?.minus(1)
                    destionationuser?.response?.removeAt(idx4)
                    destionationuser?.responsecount = destionationuser?.responsecount?.minus(1)

                }
            }
            currentuser?.let { it1 ->
                firestore?.collection("userinfo")?.document(currentemail!!)?.set(
                    it1
                )
            }
            destionationuser?.let { it1 ->
                firestore?.collection("userinfo")?.document(destionationemail!!)?.set(
                    it1
                )
            }


        }
    }

    private fun displayImageRef(imageRef : StorageReference?, view: ImageView){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {

            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener(){
            view.setImageResource(R.drawable.ic_account)
        }
    }
}

