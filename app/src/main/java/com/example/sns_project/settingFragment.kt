package com.example.sns_project

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.sns_project.databinding.FragmentHomeBinding
import com.example.sns_project.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_setting.view.*


class settingFragment : Fragment(R.layout.fragment_setting) {

    //미완성
    var firestore : FirebaseFirestore? = null
    var fragmentView : View? = null
    var uid : String? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val userCollectionRef = db.collection("userinfo")


    private lateinit var binding: FragmentSettingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = FragmentSettingBinding.bind(view)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        val mail = Firebase.auth.currentUser?.email

        storage = Firebase.storage
        val profileImage = storage.getReferenceFromUrl("gs://sns-project-c4954.appspot.com/image/${mail}/${mail}")
        displayImageRef(profileImage,binding.imageView3)
        binding.textViewmail.text = mail
        userCollectionRef.document(mail!!).get().addOnSuccessListener {
            binding.textViewName.text = it["name"].toString()
        }
        binding.imageView3.setOnClickListener {
            val intent = Intent(this.context,  MyProfileActivity::class.java)
            intent.putExtra("userMail", mail)
            startActivity(intent)
        }


        binding.logout.setOnClickListener {
            var auth : FirebaseAuth? = null
            auth = FirebaseAuth.getInstance()
            auth?.signOut()
            activity?.finish()
            startActivity(Intent(activity, LoginActivity::class.java))
        }


        binding.setting.setOnClickListener {

            //SettingPreferenceFragment()
            startActivity(Intent(activity, SettingsActivity::class.java))


                    }

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    private fun displayImageRef(imageRef : StorageReference?, view: ImageView){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {

            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener(){
            //println("Profile Image Default")
            view.setImageResource(R.drawable.ic_account)
        }
    }

}

