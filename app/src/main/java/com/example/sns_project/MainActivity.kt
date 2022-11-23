package com.example.sns_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.sns_project.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)} //기본적으로 모두 뷰바인딩 사용
    private val db: FirebaseFirestore = Firebase.firestore
    private val userCollectionRef = db.collection("userinfo")
    private var snapshotListener: ListenerRegistration? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        if(Firebase.auth.currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish() //로그인 안했으면 로그인으로
        }

        /*var firestore : FirebaseFirestore? = null
        var auth : FirebaseAuth? = null
        var currentuser : String? = null
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentuser = auth.currentUser?.uid*/







        supportFragmentManager.beginTransaction().add(binding.frame.id,homeFragment()).commit()
        //초기 프래그먼트 -> 홈 프래그먼트

        //하단 바 (홈/프로필/검색/설정)
        binding.bottomNavi.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.homeFragment -> {
                    changeFragment(homeFragment())
                    true
                }
                R.id.profileFragment -> {
                    changeFragment(profileFragment())
                    true
                }
                R.id.searchFragment -> {
                    changeFragment(searchFragment())
                    true
                }
                R.id.settingFragment -> {
                    changeFragment(settingFragment())
                    true
                }
                else -> false
            }


        }


    }

    private fun changeFragment(fragment: Fragment){
        //프래그먼트 전환

        supportFragmentManager.beginTransaction().replace(binding.frame.id,fragment).commit()
    }






}