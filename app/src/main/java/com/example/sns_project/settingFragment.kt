package com.example.sns_project

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth


class settingFragment : PreferenceFragmentCompat() {

    //미완성

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        displaySettings()
    }

    private fun displaySettings() {
        val settings = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        val reply = settings?.getString("reply", "")
        val str = """reply: $reply"""
        println(str)
    }



    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }*/


}