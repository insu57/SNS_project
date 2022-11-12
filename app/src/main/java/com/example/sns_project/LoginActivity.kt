package com.example.sns_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sns_project.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonLogin.setOnClickListener {
            val userMail = binding.editTextMail.text.toString()
            val userPass = binding.editTextPass.text.toString()
            doLogin(userMail, userPass)
        }

        binding.buttonSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java)) //회원가입으로 이돟
        }


        binding.buttonGoogle.setOnClickListener {
            //구글 로그인 미완성
        }

    }

    private fun doLogin(userMail: String, userPass: String){
        Firebase.auth.signInWithEmailAndPassword(userMail, userPass)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    startActivity( Intent(this, MainActivity::class.java))
                    finish() //로그인 성공시 메인으로 이동
                }
                else{
                    Log.w("LoginActivity","signInWithEmail",it.exception)
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }



}