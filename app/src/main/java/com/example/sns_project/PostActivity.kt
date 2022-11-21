package com.example.sns_project


import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sns_project.databinding.ActivityPostBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*


class PostActivity : AppCompatActivity() {
    private val binding by lazy {ActivityPostBinding.inflate(layoutInflater) }

    lateinit var storage: FirebaseStorage
    private var photoUri: Uri? = null
    private val userMail = Firebase.auth.currentUser?.email
    private val db: FirebaseFirestore = Firebase.firestore
    private val userinfoCollectionRef = db.collection("userinfo")
    private val userPostCollectionRef = db.collection("userPost")
    private val hashMap: HashMap<String, Any> = HashMap()

    //DB userPost / Post(Auto ID) / 시간, userMail, 이미지경로, 작성글 등

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        storage = Firebase.storage

        openGallery() //바로 갤러리 열기

        binding.buttonUpload.setOnClickListener {
            uploadImage()//이미지 업로드 + DB 업로드



            finish()//메인(홈)으로 돌아감
        }

        binding.buttonCancel.setOnClickListener {

            finish()//메인(홈)으로 돌아감
        }

    }

    private fun openGallery() {
        val loadImage = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            binding.imageView.setImageURI(it)
            photoUri= it
        })

        loadImage.launch("image/*")

    }






    private fun uploadImage(){

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMAGE_$timestamp"
        val userMail =  Firebase.auth.currentUser?.email
        val storageRef = storage.reference
        val imageRef = storageRef.child("image/$userMail/$fileName")




        //gs://sns-project-c4954.appspot.com/image/userMail -> 파이어스토리지 이미지 경로 - Url

        hashMap["userMail"] = userMail!!
        hashMap["imagePath"] = "gs://sns-project-c4954.appspot.com/image/$userMail/$fileName"
        hashMap["timestamp"] = timestamp
        userinfoCollectionRef.document(userMail).get().addOnSuccessListener {
            hashMap["name"] = it["name"].toString()
        }
        hashMap["text"] =  binding.editText.text.toString()

        imageRef.putFile(photoUri!!).addOnCompleteListener{
            userPostCollectionRef.document().set(hashMap)
        }


    }




}