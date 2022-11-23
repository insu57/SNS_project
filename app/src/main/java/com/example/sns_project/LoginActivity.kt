package com.example.sns_project

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sns_project.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(){
    var mGoogleSignInClient : GoogleSignInClient? = null
    lateinit var auth : FirebaseAuth


    private val db: FirebaseFirestore = Firebase.firestore
    private val userinfoCollectionRef = db.collection("userinfo")
    private val hashMap: HashMap<String, Any> = HashMap()
    var firestore : FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth


        binding.buttonLogin.setOnClickListener {
            val userMail = binding.editTextMail.text.toString()
            val userPass = binding.editTextPass.text.toString()
            doLogin(userMail, userPass)
        }

        binding.buttonSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java)) //회원가입으로 이돟
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("1072903648658-1sn19eev2r6m0fc2uag3q7uescte8m8n.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)




        binding.buttonGoogle.setOnClickListener { // googlelogin

            googleLogin()
        }



    }


    var googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == -1) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }catch(e:ApiException){

            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        var input : ArrayList<String> = arrayListOf()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                task -> if(task.isSuccessful){
                    val user = auth.currentUser
                user?.let{
                    val email = user.email
                    val name = user.displayName // info
                    val hashMap = hashMapOf(
                        "Name" to name.toString(),
                        "friend" to 0,
                        "request" to input,
                        "requestcount" to 0,
                        "response" to input,
                        "responsecount" to 0,
                        "show" to "all"
                    )
                        //["Name"] = name.toString()
                    var userArr : ArrayList<userDTO> = arrayListOf() // 유저 정보 담을 배열
                    var userList : ArrayList<String> = arrayListOf() // 유저의 이메일(id값)을 담을 배열
                    var count = 0


                    firestore = FirebaseFirestore.getInstance()

                        firestore?.collection("userinfo")?.addSnapshotListener {
                                querySnapshot, firebaseFirestoreException ->
                            userArr.clear()
                            userList.clear()

                            for(snapshot in querySnapshot!!.documents){

                                var item = snapshot.toObject(userDTO::class.java)
                                if(email == snapshot.id){
                                    println("countfirst" + count)
                                    count = 1
                                }
                                println(snapshot.id)
                                println("이메일 " + email)
                            }
                            if (email != null && count == 0) {

                                userinfoCollectionRef.document(email).set(hashMap)


                            }
                        }




                }
                startActivity( Intent(this, MainActivity::class.java))
                finish() //로그인 성공시 메인으로 이동
            }
                else{
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
            }
    }



    fun googleLogin(){
        val signInIntent = mGoogleSignInClient!!.signInIntent
        googleLoginLauncher.launch(signInIntent)
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