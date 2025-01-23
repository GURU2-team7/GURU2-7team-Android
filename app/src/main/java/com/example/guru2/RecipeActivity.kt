package com.example.guru2

import android.graphics.Color
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe)

        // 첫 번째 라디오 그룹
        val radioGroup: RadioGroup = findViewById(R.id.radio_group1)

        // 라디오 버튼들
        val radioButtonHansik: RadioButton = findViewById(R.id.radioButton_hansik)
        val radioButtonYangshik: RadioButton = findViewById(R.id.radioButton_yangshik)
        val radioButtonJoongshik: RadioButton = findViewById(R.id.radioButton_joongshik)
        val radioButtonIlshik: RadioButton = findViewById(R.id.radioButton_ilshik)
        val radioButtonJaegwajepang: RadioButton = findViewById(R.id.radioButton_jaegwajepang)
        val radioButtonWonpan: RadioButton = findViewById(R.id.radioButton_wonpan)
        val radioButtonGansik: RadioButton = findViewById(R.id.radioButton_gansik)
        val radioButtonDiet: RadioButton = findViewById(R.id.radioButton_diet)


        // 라디오 버튼 클릭 시 배경 색을 바꾸는 리스너
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // 모든 버튼의 배경을 초기화
            resetBackgroundColor(
                radioButtonHansik,
                radioButtonYangshik,
                radioButtonJoongshik,
                radioButtonIlshik,
                radioButtonJaegwajepang,
                radioButtonWonpan,
                radioButtonGansik,
                radioButtonDiet
            )

            // 선택된 라디오 버튼의 배경 색을 변경 (상태별 배경 적용)
            val selectedButton: RadioButton = findViewById(checkedId)
            selectedButton.setBackgroundResource(R.drawable.radio_button_selected) // 둥근 배경 적용
            selectedButton.invalidate()
        }

        // 시스템 바 인셋 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun resetBackgroundColor(vararg buttons: RadioButton) {
        buttons.forEach { it.setBackgroundResource(R.drawable.radio_button_unselected) }
    }

}
