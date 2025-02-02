package com.example.guru2.recipe

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
import com.example.guru2.db.SavedRecipe
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private var savedRecipe: SavedRecipe? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        // 1) RECIPE_ID 받기
        val recipeId = arguments?.getInt("RECIPE_ID", -1) ?: -1
        if (recipeId == -1) {
            Toast.makeText(requireContext(), "레시피 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
            return
        }

        // 2) DB에서 레시피 정보 불러오기
        savedRecipe = dbHelper.getSavedRecipeById(recipeId)
        if (savedRecipe == null) {
            Toast.makeText(requireContext(), "레시피 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
            return
        }

        Log.d("RecipeDetailFragment", "불러온 레시피: ${savedRecipe!!.recipeName}")

        // 뒤로가기
        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // 드롭다운
        val openDropDown = binding.openDropDown
        val dropDownMenu = binding.layoutDropDownMenu
        val dropdownX = binding.dropdownX
        val dropdownBookmark = binding.dropdownBookmark
        val dropdownTimer = binding.dropdownTimer

        openDropDown.setOnClickListener {
            openDropDown.visibility = View.GONE
            dropDownMenu.visibility = View.VISIBLE
        }
        dropdownX.setOnClickListener {
            dropDownMenu.visibility = View.GONE
            openDropDown.visibility = View.VISIBLE
        }

        dropdownBookmark.setImageResource(R.drawable.dropdown_bookmark_filled)
        dropdownBookmark.setOnClickListener {
            val targetName = savedRecipe!!.recipeName
            val rows = dbHelper.deleteSavedRecipe(targetName)
            if (rows > 0) {
                Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                dropdownBookmark.setImageResource(R.drawable.dropdown_bookmark_unfilled)
            } else {
                Toast.makeText(requireContext(), "삭제 실패 / 이미 삭제됨", Toast.LENGTH_SHORT).show()
            }
        }
        dropdownTimer.setOnClickListener {
            Toast.makeText(requireContext(), "타이머 페이지로 이동(미구현)", Toast.LENGTH_SHORT).show()
        }

        setupUI()
    }

    private fun setupUI() {
        val recipe = savedRecipe ?: return

        // (1) 제목
        binding.editTextSearch.apply {
            setText(recipe.recipeName)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setTypeface(null, Typeface.BOLD)
        }

        // (2) 수정 버튼
        binding.buttonSearch.setOnClickListener {
            val newName = binding.editTextSearch.text.toString().trim()
            if (newName.isNotEmpty()) {
                updateRecipeInDB(newName)
            } else {
                Toast.makeText(requireContext(), "레시피 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // (3) 레시피 시간 → **표시 안 함** (주석 or 빈 문자열)
        // binding.textView20m.text = recipe.cookingTime
        binding.textView20m.text = ""  // 아예 공백 설정

        // (4) 재료
        val ingLayout = binding.linearLayoutScrollViewRecipeDetailIngredients
        ingLayout.removeAllViews()
        ingLayout.setPadding(30, 20, 30, 20)
        val ingText = TextView(requireContext()).apply {
            text = recipe.ingredients
            textSize = 15f
        }
        ingLayout.addView(ingText)

        // (5) 조리방법
        val cookLayout = binding.linearLayoutScrollViewRecipeDetailCook
        cookLayout.removeAllViews()
        cookLayout.setPadding(30, 20, 30, 20)

        val steps = recipe.recipeText.split(Regex("(?=\\d+\\.)"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (steps.isEmpty()) {
            val recipeTextView = TextView(requireContext()).apply {
                text = recipe.recipeText
                textSize = 15f
            }
            cookLayout.addView(recipeTextView)
        } else {
            for (step in steps) {
                val stepText = TextView(requireContext()).apply {
                    text = step
                    textSize = 15f
                }
                cookLayout.addView(stepText)
            }
        }

        // (6) 칼로리
        val calLayout = binding.linearLayoutScrollViewCalorie
        calLayout.removeAllViews()
        calLayout.setPadding(30, 20, 30, 20)
        val calText = TextView(requireContext()).apply {
            text = recipe.calorie
            textSize = 15f
        }
        calLayout.addView(calText)

        // (7) 영양성분
        val nutLayout = binding.linearLayoutScrollViewNutrients
        nutLayout.removeAllViews()
        nutLayout.setPadding(30, 20, 30, 20)
        val nutText = TextView(requireContext()).apply {
            text = recipe.nutrient
            textSize = 15f
        }
        nutLayout.addView(nutText)
    }

    private fun updateRecipeInDB(newTitle: String) {
        val recipe = savedRecipe ?: return

        if (newTitle == recipe.recipeName) {
            Toast.makeText(requireContext(), "기존 제목과 동일합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val newSaveDate = getCurrentDate()
        val rows = dbHelper.updateSavedRecipe(
            recipe.recipeName,
            newTitle,
            recipe.cookingTime,
            recipe.ingredients,
            recipe.recipeText,
            recipe.calorie,
            recipe.nutrient,
            newSaveDate
        )

        if (rows > 0) {
            Toast.makeText(requireContext(), "레시피 제목을 수정했습니다.", Toast.LENGTH_SHORT).show()
            savedRecipe = recipe.copy(recipeName = newTitle)
        } else {
            Toast.makeText(requireContext(), "수정 실패.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
