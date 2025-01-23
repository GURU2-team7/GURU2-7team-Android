package com.example.guru2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Edge-to-Edge 설정 유지
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // AllergyActivity로 이동 버튼 이벤트
        val buttonGoToAllergy: Button = findViewById(R.id.buttonGoToAllergy)
        buttonGoToAllergy.setOnClickListener {
            val intent = Intent(this, AllergyActivity::class.java)
            startActivity(intent)
        }

        // RecipeActivity로 이동 버튼 이벤트
        val buttonGoToRecipe: Button = findViewById(R.id.buttonGoToAllergy)
        buttonGoToRecipe.setOnClickListener {
            val intent = Intent(this, RecipeActivity::class.java)
            startActivity(intent)
        }

        // 레시피 요청 페이지로 이동 버튼 - sample
        val buttonGoToAskRecipe: Button = findViewById(R.id.buttonGoToAskRecipe)
        buttonGoToAskRecipe.setOnClickListener {
            val intent = Intent(this, AskRecipeActivity::class.java)
            startActivity(intent)
        }
    }
}
