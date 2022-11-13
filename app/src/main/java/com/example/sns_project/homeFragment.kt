package com.example.sns_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sns_project.databinding.FragmentHomeBinding

class homeFragment : Fragment(R.layout.fragment_home) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)
        binding.buttonPost.setOnClickListener {
            startActivity(Intent(activity,PostActivity::class.java)) //버튼클릭 시 포스트로 이동
        }

        binding.button2.setOnClickListener{
            startActivity(Intent(activity,CommentActivity::class.java))
        }
        //DB에서 가져와서 리사이클러뷰에 표시

    }




}