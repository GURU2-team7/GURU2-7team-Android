package com.example.guru2.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guru2.R

class Recipe1 : Fragment(R.layout.activity_recipe1) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 버튼을 가져와 클릭 리스너 설정
        val myButton = view.findViewById<Button>(R.id.button_submit)
        myButton.setOnClickListener {
            // 버튼 클릭 시 Toast 메시지 표시
            Toast.makeText(requireContext(), "버튼을 눌렀습니다!", Toast.LENGTH_SHORT).show()
        }
    }
}
