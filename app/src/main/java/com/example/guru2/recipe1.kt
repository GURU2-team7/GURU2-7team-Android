package com.example.guru2

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class recipe1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 엣지 투 엣지 활성화
        setContentView(R.layout.activity_recipe1)

        // 메인 뷰를 안전하게 가져오고 인셋 처리
        val mainView = findViewById<View>(R.id.main)

        if (mainView != null) {
            // 시스템 바 인셋 처리
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } else {
            // mainView가 null일 경우 로그를 남기거나 처리
            println("main view is null")
        }
    }
}
