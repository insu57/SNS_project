package com.example.sns_project

import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sns_project.databinding.ActivitySignupBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity: AppCompatActivity() {

    private val db: FirebaseFirestore = Firebase.firestore
    private val userinfoCollectionRef = db.collection("userinfo")
    private val hashMap: HashMap<String, Any> = HashMap()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener {
            val userName = binding.editTextName.text.toString()
            val userMail = binding.editTextMail.text.toString()
            val userPass = binding.editTextPass.text.toString()

            doSignup(userName, userMail, userPass)
        }

    }

    private fun doSignup(userName: String,userMail: String, userPass: String){
        var input : ArrayList<String> = arrayListOf()
        val hashMap = hashMapOf(
            "Name" to userName,
            "friend" to 0,
            "request" to input,
            "requestcount" to 0,
            "response" to input,
            "responsecount" to 0,
            "show" to "all",
        )


        Firebase.auth.createUserWithEmailAndPassword(userMail, userPass)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    startActivity(Intent(this, MainActivity::class.java)) //메인으로 이동
                    finish()
                }
                else{
                    Log.w("SignupActivity","createWithEmail",it.exception)
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }


        userinfoCollectionRef.document(userMail).set(hashMap) //DB에 userinfo/userMail/ 에 Name:userName 저장
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                }
                else{
                    Log.w("SignupActivity","userNameDB Upload",it.exception)
                    Toast.makeText(this, "이름 저장 실패", Toast.LENGTH_SHORT).show()
                }
            }



    }




}