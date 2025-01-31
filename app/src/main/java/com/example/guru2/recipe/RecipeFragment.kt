package com.example.guru2.recipe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.guru2.databinding.FragmentRecipeBinding
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.guru2.MainActivity
import com.example.guru2.R
import com.example.guru2.db.DatabaseHelper
import com.example.guru2.home.HomeFragment


class RecipeFragment : Fragment(R.layout.fragment_recipe) {
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var radioGroupCuisine: RadioGroup
    private lateinit var radioGroupCookingWay: RadioGroup
    private lateinit var radioGroupTime: RadioGroup
    private lateinit var checkBoxIngredientOption: LinearLayout  // 체크박스 변수 추가
    private lateinit var buttonSubmit: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRecipeBinding.bind(view)

        dbHelper = DatabaseHelper(requireContext())

        // XML에서 라디오 그룹과 버튼 연결
        radioGroupCuisine = view.findViewById(R.id.radio_group1)
        radioGroupCookingWay = view.findViewById(R.id.radio_group2)
        radioGroupTime = view.findViewById(R.id.radio_group3)
        buttonSubmit = view.findViewById(R.id.button_submit)
        checkBoxIngredientOption = view.findViewById(R.id.checkbox_ingredient)  // XML 체크박스 ID 연결

        checkBoxIngredientOption = view.findViewById(R.id.checkbox_ingredient)  // 체크박스 레이아웃

        // 전달받은 재료 리스트 가져오기
        val ingredients = arguments?.getStringArray("ingredientList") ?: emptyArray()



        buttonSubmit.setOnClickListener {
            sendSelectedOptions()
        }

        val backArrowButton = view.findViewById<ImageView>(R.id.backArrow)

        backArrowButton.setOnClickListener {
            // 메인 화면으로 돌아가는 Intent 생성
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)  // MainActivity로 이동
            activity?.finish()  // 현재 Activity 종료}
        }

        loadIngredientsFromFridge() // 냉장고 DB에서 재료 가져와 체크박스 추가
    }

    private fun sendSelectedOptions() {
        val selectedCuisine = getSelectedRadioText(radioGroupCuisine)
        val selectedCookingWay = getSelectedRadioText(radioGroupCookingWay)
        val selectedTime = getSelectedRadioText(radioGroupTime)

        // 체크박스에서 선택된 재료들 가져오기
        val selectedIngredientsList = mutableListOf<String>()

        // LinearLayout 안의 모든 CheckBox 확인
        for (i in 0 until checkBoxIngredientOption.childCount) {
            val checkBox = checkBoxIngredientOption.getChildAt(i) as CheckBox
            if (checkBox.isChecked) {
                selectedIngredientsList.add(checkBox.text.toString())  // 선택된 체크박스 텍스트 추가
            }
        }

        // 선택되지 않은 항목 확인
        if (selectedCuisine == "선택 안됨" || selectedCookingWay == "선택 안됨" || selectedTime == "선택 안됨" || selectedIngredientsList.isEmpty()) {
            Toast.makeText(requireContext(), "모든 옵션을 선택해주세요!", Toast.LENGTH_SHORT).show()
            return
        }

        // 선택된 값들을  AskRecipeActivity로 전달
        val intent = Intent(requireContext(), AskRecipeActivity::class.java).apply {
            putExtra("cuisine", selectedCuisine)  // 요리 종류
            putExtra("cookingWay", selectedCookingWay)  // 요리 방식
            putExtra("time", selectedTime)  // 요리 시간
            putExtra("ingredients", selectedIngredientsList.joinToString(", "))  // 선택된 재료들을 쉼표로 구분하여 전달
        }


        startActivity(intent)
    }

    private fun getSelectedRadioText(radioGroup: RadioGroup): String {
        val selectedId = radioGroup.checkedRadioButtonId
        return if (selectedId != -1) {
            val radioButton = radioGroup.findViewById<RadioButton>(selectedId)
            radioButton.text.toString()
        } else {
            "선택 안됨"
        }
    }



    private fun loadIngredientsFromFridge() {
        val ingredientList = dbHelper.getAllFridgeItems() // 냉장고 DB에서 모든 재료 가져오기
        checkBoxIngredientOption.removeAllViews() // 기존 체크박스 초기화

        for (ingredient in ingredientList) {
            val checkBox = CheckBox(requireContext()).apply {
                text = ingredient.name  // 체크박스의 텍스트를 재료명으로 설정
                textSize = 16f
            }
            checkBoxIngredientOption.addView(checkBox) // LinearLayout에 추가
        }
    }
}



