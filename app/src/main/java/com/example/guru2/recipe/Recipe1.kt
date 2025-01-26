package com.example.guru2.recipe

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guru2.R

class Recipe1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe1)

        // findViewById로 버튼 가져오기
        val myButton = findViewById<Button>(R.id.button_submit) // 버튼 id: myButton

        // 버튼 클릭 리스너 설정
        myButton.setOnClickListener {
            // 버튼 클릭 시 Toast 메시지 표시
            Toast.makeText(this, "버튼을 눌렀습니다!", Toast.LENGTH_SHORT).show()
        }

        // 시스템 바 인셋 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }




}