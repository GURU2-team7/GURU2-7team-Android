package com.example.guru2.recipe

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guru2.R
import com.example.guru2.databinding.FragmentRecipeDetailBinding
import com.example.guru2.db.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper

    // 레시피 정보들 (북마크 페이지에서 넘어온다고 가정)
    private var originalName: String = ""   // DB 수정 시, 기존 제목 식별용
    private var cookingTime: String = ""
    private var ingredients: String = ""
    private var recipeText: String = ""
    private var calorie: String = ""
    private var nutrient: String = ""
    private var savedDate: String = ""      // 저장된 날짜 (if needed)

    // 레이아웃 초기화
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 실제 UI 로직
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        // 1) 인자(번들)로부터 기존 레시피 정보를 가져온다.
        // 북마크 페이지 등에서 arguments?.putString(...)로 넘겼다고 가정
        originalName = arguments?.getString("recipeName") ?: ""
        cookingTime = arguments?.getString("cookingTime") ?: ""
        ingredients = arguments?.getString("ingredients") ?: ""
        recipeText = arguments?.getString("recipeText") ?: ""
        calorie = arguments?.getString("calorie") ?: ""
        nutrient = arguments?.getString("nutrient") ?: ""
        savedDate = arguments?.getString("saveDate") ?: ""

        Log.d("RecipeDetailFragment",
            "불러온 레시피: name=$originalName, time=$cookingTime, date=$savedDate"
        )

        // 뒤로가기 아이콘
        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // 세 줄 메뉴 버튼 & 드롭다운
        val openDropDown = binding.openDropDown
        val dropDownMenu = binding.layoutDropDownMenu

        // 드롭다운 내부 버튼
        val dropdownX = binding.dropdownX
        val dropdownBookmark = binding.dropdownBookmark
        val dropdownTimer = binding.dropdownTimer

        // (A) 세 줄 → 드롭다운 열기
        openDropDown.setOnClickListener {
            openDropDown.visibility = View.GONE
            dropDownMenu.visibility = View.VISIBLE
        }

        // (B) X → 드롭다운 닫기
        dropdownX.setOnClickListener {
            dropDownMenu.visibility = View.GONE
            openDropDown.visibility = View.VISIBLE
        }

        // (C) 북마크 아이콘 (기본 검정) → 클릭 시 삭제
        dropdownBookmark.setOnClickListener {
            // DB에서 삭제
            val rows = dbHelper.deleteSavedRecipe(originalName)
            if (rows > 0) {
                Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
                // 아이콘을 unfilled로 변경
                dropdownBookmark.setImageResource(R.drawable.dropdown_bookmark_unfilled)
            } else {
                Toast.makeText(requireContext(), "삭제 실패 / 이미 삭제됨", Toast.LENGTH_SHORT).show()
            }
        }

        // (D) 타이머 버튼
        dropdownTimer.setOnClickListener {
            Toast.makeText(requireContext(), "타이머 페이지로 이동(미구현)", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(requireContext(), TimerActivity::class.java)) etc.
        }

        // 2) UI에 값 표시
        // EditText(제목) - 수정 가능
        binding.editTextSearch.apply {
            setText(originalName)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setTypeface(null, Typeface.BOLD)
        }

        // 요리 시간
        binding.textView20m.text = cookingTime

        // 재료 표시
        val ingLayout = binding.linearLayoutScrollViewRecipeDetailIngredients
        ingLayout.removeAllViews()
        ingLayout.setPadding(30, 20, 30, 20)
        val ingText = TextView(requireContext()).apply {
            text = ingredients
            textSize = 15f
        }
        ingLayout.addView(ingText)

        // 조리방법 표시
        val cookLayout = binding.linearLayoutScrollViewRecipeDetailCook
        cookLayout.removeAllViews()
        cookLayout.setPadding(30, 20, 30, 20)

        // 예: 레시피텍스트가 "1. ~~\n2. ~~" 형태일 수 있음
        val steps = recipeText.split(Regex("(?=\\d+\\.)"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (steps.isEmpty()) {
            val recipeTextView = TextView(requireContext()).apply {
                text = recipeText
                textSize = 15f
            }
            cookLayout.addView(recipeTextView)
        } else {
            steps.forEach { step ->
                val stepText = TextView(requireContext()).apply {
                    text = step
                    textSize = 15f
                }
                cookLayout.addView(stepText)
            }
        }

        // 칼로리
        val calLayout = binding.linearLayoutScrollViewCalorie
        calLayout.removeAllViews()
        calLayout.setPadding(30, 20, 30, 20)
        val calTextView = TextView(requireContext()).apply {
            text = calorie
            textSize = 15f
        }
        calLayout.addView(calTextView)

        // 영양성분
        val nutLayout = binding.linearLayoutScrollViewNutrients
        nutLayout.removeAllViews()
        nutLayout.setPadding(30, 20, 30, 20)
        val nutTextView = TextView(requireContext()).apply {
            text = nutrient
            textSize = 15f
        }
        nutLayout.addView(nutTextView)

        // 3) "수정" 버튼 (연필 아이콘)
        binding.buttonSearch.setOnClickListener {
            val newName = binding.editTextSearch.text.toString().trim()
            if (newName.isNotEmpty()) {
                // DB 업데이트
                updateRecipeInDB(newName)
            } else {
                Toast.makeText(requireContext(), "레시피 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 레시피 제목(등)을 수정할 때 DB에 반영하는 메서드
    private fun updateRecipeInDB(newTitle: String) {
        // 원하는 시점에 저장날짜를 다시 갱신할 수도 있음
        val newSaveDate = getCurrentDate()
        // DB update
        val rows = dbHelper.updateSavedRecipe(
            originalName = originalName,   // 기존 식별 (바뀔 전 이름)
            newName = newTitle,            // 사용자가 바꾼 이름
            cookingTime = cookingTime,     // 필요하면 UI에서 새 값 받거나 유지
            ingredients = ingredients,
            recipeText = recipeText,
            calorie = calorie,
            nutrient = nutrient,
            newSaveDate = newSaveDate
        )

        if (rows > 0) {
            Toast.makeText(requireContext(), "레시피 제목을 수정했습니다.", Toast.LENGTH_SHORT).show()
            // originalName 갱신 (다음 수정 시 식별)
            originalName = newTitle
        } else {
            Toast.makeText(requireContext(), "수정 실패.", Toast.LENGTH_SHORT).show()
        }
    }

    // 날짜 포맷 (yyyy-MM-dd)
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
