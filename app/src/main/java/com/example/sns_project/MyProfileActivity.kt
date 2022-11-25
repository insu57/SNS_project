package com.example.sns_project

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.sns_project.databinding.ActivityMyProfileBinding

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.HashMap

class MyProfileActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = Firebase.firestore
    private val userinfoCollectionRef = db.collection("userinfo")
    private val userPostCollectionRef = db.collection("userPost")
    private val binding by lazy { ActivityMyProfileBinding.inflate(layoutInflater) }
    private var photoUri: Uri? = null
    private val hashMap: HashMap<String, Any> = HashMap()

    private val myMail = Firebase.auth.currentUser?.email


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userMail = intent.getStringExtra("userMail")


        println("!!!$userMail")
        println("MY!!!$myMail")
        if(userMail.equals(myMail)){
            binding.buttonProfile.visibility = View.VISIBLE
            println("IMAGE   VISIBLE")
        }


        setContentView(binding.root)

        storage = Firebase.storage




        val profileImage = storage.getReferenceFromUrl("gs://sns-project-c4954.appspot.com/image/${userMail}/${userMail}")
        binding.profileMail.text = userMail

        userinfoCollectionRef.document(userMail!!).get().addOnSuccessListener {
            binding.profileName.text = it["name"].toString()
        }

        displayImageRef(profileImage, binding.imageViewProfile)

        val loadImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {

                photoUri= it
                uploadImage()
            })


        binding.buttonProfile.setOnClickListener {


            loadImage.launch("image/*")
            binding.imageViewProfile.setImageURI(photoUri)

        }





    }






    private fun uploadImage(){
        val userMail =  Firebase.auth.currentUser?.email
        val storageRef = storage.reference
        val imageRef = storageRef.child("image/$userMail/$userMail")

        hashMap["ProfileImage"] = userMail!!

        imageRef.putFile(photoUri!!).addOnSuccessListener {
           //
        }

    }
    private fun displayImageRef(imageRef : StorageReference?, view: ImageView){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener{
            println("img null")
        }
    }


}