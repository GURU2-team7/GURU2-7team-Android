package com.example.guru2.recipe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.guru2.MainActivity
import com.example.guru2.R
import com.example.guru2.databinding.FragmentRecipeBinding
import com.example.guru2.db.DatabaseHelper

class RecipeFragment : Fragment(R.layout.fragment_recipe) {
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var radioGroupCuisine: RadioGroup
    private lateinit var radioGroupCookingWay: RadioGroup
    private lateinit var radioGroupTime: RadioGroup
    private lateinit var checkBoxIngredientOption: LinearLayout
    private lateinit var buttonSubmit: Button

    private lateinit var dbHelper: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRecipeBinding.bind(view)

        // DBHelper 초기화
        dbHelper = DatabaseHelper(requireContext())

        // 라디오 그룹 및 버튼 연결
        radioGroupCuisine      = view.findViewById(R.id.radio_group1)
        radioGroupCookingWay   = view.findViewById(R.id.radio_group2)
        radioGroupTime         = view.findViewById(R.id.radio_group3)
        checkBoxIngredientOption = view.findViewById(R.id.checkbox_ingredient)
        buttonSubmit           = view.findViewById(R.id.button_submit)

        // Submit 버튼 → RecipeResponseFragment 전환
        buttonSubmit.setOnClickListener {
            sendSelectedOptions()
        }

        // 뒤로가기 아이콘 → MainActivity로
        val backArrowButton = binding.backArrow
        backArrowButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } // 현재 액티비티 종료


        // 냉장고 DB에서 재료 가져와 체크박스 추가
        loadIngredientsFromFridge()
    }

    /**
     * 사용자 선택 옵션 수집 → RecipeResponseFragment로 이동
     */
    private fun sendSelectedOptions() {
        val selectedCuisine    = getSelectedRadioText(radioGroupCuisine)
        val selectedCookingWay = getSelectedRadioText(radioGroupCookingWay)
        val selectedTime       = getSelectedRadioText(radioGroupTime)

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

        // Bundle에 전달할 값 저장
        val bundle = Bundle().apply {
            putString("cuisine",     selectedCuisine)
            putString("cookingWay",  selectedCookingWay)
            putString("time",        selectedTime)
            putString("ingredients", selectedIngredientsList.joinToString(", "))
        }

        // RecipeResponseFragment 인스턴스 생성 & arguments
        val responseFragment = RecipeResponseFragment().apply {
            arguments = bundle
        }

        // Fragment 전환
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.rootlayout, responseFragment)
            .addToBackStack(null)
            .commit()
    }

    // 현재 라디오그룹에서 선택된 라디오버튼 텍스트
    private fun getSelectedRadioText(radioGroup: RadioGroup): String {
        val selectedId = radioGroup.checkedRadioButtonId
        return if (selectedId != -1) {
            val radioButton = radioGroup.findViewById<RadioButton>(selectedId)
            radioButton.text.toString()
        } else {
            "선택 안됨"
        }
    }

    /**
     * DB에서 냉장고 재료 목록을 불러와 체크박스를 생성
     */
    private fun loadIngredientsFromFridge() {
        val ingredientList = dbHelper.getAllFridgeItems()  // 냉장고 DB에서 모든 재료
        checkBoxIngredientOption.removeAllViews()          // 기존 체크박스 초기화

        // 동적으로 CheckBox 생성
        for (ingredient in ingredientList) {
            val checkBox = CheckBox(requireContext()).apply {
                text = ingredient.name
                textSize = 16f
            }
            checkBoxIngredientOption.addView(checkBox)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
