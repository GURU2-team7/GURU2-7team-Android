package com.example.guru2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guru2.databinding.ActivityMainBinding // 뷰 바인딩 클래스 임포트

class MainActivity : AppCompatActivity() {

    // 뷰 바인딩 객체 선언
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-Edge 설정 유지
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // AllergyActivity로 이동 버튼 이벤트
        binding.buttonGoToAllergy.setOnClickListener {
            val intent = Intent(this, AllergyActivity::class.java)
            startActivity(intent)
        }

        // FridgeActivity로 이동 버튼 이벤트
        binding.buttonGoToFridge.setOnClickListener {
            val intent = Intent(this, FridgeActivity::class.java)
            startActivity(intent)
        }
    }
}
