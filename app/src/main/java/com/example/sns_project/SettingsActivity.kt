package com.example.sns_project

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.sns_project.databinding.ActivityMainBinding
import com.example.sns_project.databinding.ActivityMyProfileBinding
import com.example.sns_project.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SettingsActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val userCollectionRef = db.collection("userinfo")
    lateinit var settingsFragment : SettingsFragment
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setResult(RESULT_OK)

        binding.Setting.setOnClickListener {
            updateSetting()
        }





    }

            private fun updateSetting() {
                val settings = PreferenceManager.getDefaultSharedPreferences(this)
                val reply = settings?.getString("reply", "")
                val str = """$reply"""
                val itemID = Firebase.auth.currentUser?.email
                if (itemID != null) {
                    userCollectionRef.document(itemID).update("show", str)

                }

            }



    class SettingsFragment : PreferenceFragmentCompat() {

        var firestore : FirebaseFirestore? = null
        private val db: FirebaseFirestore = Firebase.firestore
        private val userCollectionRef = db.collection("userinfo")
        var Settingpreference: Preference? = null


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            var userSetting : String? = "all" // 유저의 이메일(id값)을 담을 배열
            var checkuser = FirebaseAuth.getInstance().currentUser?.email // 자신의 이메일 정보
            // 모음
            firestore?.collection("userinfo")?.addSnapshotListener {
                    querySnapshot, firebaseFirestoreException ->


                for(snapshot in querySnapshot!!.documents){

                    var item = snapshot.toObject(userDTO::class.java)
                    if(checkuser == snapshot.id){
                        if (item != null) {
                            userSetting = item.show
                        }

                    }
                }


            }





            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            Settingpreference?.key = userSetting
            println(userSetting)

        }
    }
}