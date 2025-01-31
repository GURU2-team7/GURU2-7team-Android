package com.example.guru2.recipe

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.guru2.R
import com.example.guru2.databinding.FragmentRecipeBinding

class RecipeFragment : Fragment(R.layout.fragment_recipe) {
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var radioGroupCuisine: RadioGroup
    private lateinit var radioGroupCookingWay: RadioGroup
    private lateinit var radioGroupTime: RadioGroup
    private lateinit var checkBoxIngredientOption: LinearLayout
    private lateinit var buttonSubmit: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRecipeBinding.bind(view)

        // 라디오 그룹 및 버튼 연결
        radioGroupCuisine = view.findViewById(R.id.radio_group1)
        radioGroupCookingWay = view.findViewById(R.id.radio_group2)
        radioGroupTime = view.findViewById(R.id.radio_group3)
        checkBoxIngredientOption = view.findViewById(R.id.checkbox_ingredient)
        buttonSubmit = view.findViewById(R.id.button_submit)

        // Submit 버튼 → RecipeResponseFragment 전환
        buttonSubmit.setOnClickListener {
            sendSelectedOptions()
        }

        // 뒤로가기 아이콘 클릭 → 이전 화면(또는 MainActivity)으로
        val backArrowButton = view.findViewById<ImageView>(R.id.backArrow)
        backArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    /**
     * 사용자 선택 옵션 수집 → RecipeResponseFragment로 전달
     */
    private fun sendSelectedOptions() {
        val selectedCuisine      = getSelectedRadioText(radioGroupCuisine)
        val selectedCookingWay   = getSelectedRadioText(radioGroupCookingWay)
        val selectedTime         = getSelectedRadioText(radioGroupTime)

        // 체크박스에서 선택된 재료들
        val selectedIngredientsList = mutableListOf<String>()
        for (i in 0 until checkBoxIngredientOption.childCount) {
            val checkBox = checkBoxIngredientOption.getChildAt(i) as CheckBox
            if (checkBox.isChecked) {
                selectedIngredientsList.add(checkBox.text.toString())
            }
        }

        // 모든 항목 선택 여부 검사
        if (selectedCuisine == "선택 안됨" ||
            selectedCookingWay == "선택 안됨" ||
            selectedTime == "선택 안됨" ||
            selectedIngredientsList.isEmpty()
        ) {
            Toast.makeText(requireContext(), "모든 옵션을 선택해주세요!", Toast.LENGTH_SHORT).show()
            return
        }

        // (A) 전달할 데이터 Bundle에 담기
        val bundle = Bundle().apply {
            putString("cuisine", selectedCuisine)
            putString("cookingWay", selectedCookingWay)
            putString("time", selectedTime)
            putString("ingredients", selectedIngredientsList.joinToString(", "))
        }

        // (B) RecipeResponseFragment 인스턴스 생성 & arguments
        val responseFragment = RecipeResponseFragment().apply {
            arguments = bundle
        }

        // (C) replace(R.id.rootlayout, responseFragment)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.rootlayout, responseFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getSelectedRadioText(radioGroup: RadioGroup): String {
        val selectedId = radioGroup.checkedRadioButtonId
        return if (selectedId != -1) {
            radioGroup.findViewById<RadioButton>(selectedId).text.toString()
        } else {
            "선택 안됨"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
