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
import com.example.guru2.api.RecipeReqData
import com.example.guru2.api.RecipeResData
import com.example.guru2.api.RetrofitClient
import com.example.guru2.api.RetrofitService
import com.example.guru2.databinding.FragmentReciperesponseBinding
import com.example.guru2.db.DatabaseHelper
import com.example.guru2.timer.TimerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeResponseFragment : Fragment() {

    private var _binding: FragmentReciperesponseBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private val service: RetrofitService by lazy {
        RetrofitClient.getClient().create(RetrofitService::class.java)
    }

    // API 응답을 저장해둘 변수 (북마크 시 사용)
    private var currentRecipe: RecipeResData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReciperesponseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())

        // RecipeFragment에서 전달된 옵션값
        val selectedCuisine     = arguments?.getString("cuisine") ?: "없음"
        val selectedCookingWay  = arguments?.getString("cookingWay") ?: "없음"
        val selectedTime        = arguments?.getString("time") ?: "없음"
        val selectedIngredients = arguments?.getString("ingredients") ?: "없음"

        Log.d("RecipeResponseFragment",
            "전달된 값: cuisine=$selectedCuisine, cookingWay=$selectedCookingWay, time=$selectedTime, ingredients=$selectedIngredients"
        )

        // 뒤로가기
        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // 드롭다운 관련
        val openDropDown = binding.openDropDown
        val dropDownMenu = binding.layoutDropDownMenu
        val dropdownX = binding.dropdownX
        val dropdownBookmark = binding.dropdownBookmark
        val dropdownTimer = binding.dropdownTimer

        // 세 줄 버튼 → 드롭다운 열기
        openDropDown.setOnClickListener {
            openDropDown.visibility = View.GONE
            dropDownMenu.visibility = View.VISIBLE
        }

        // X 버튼 → 닫기
        dropdownX.setOnClickListener {
            dropDownMenu.visibility = View.GONE
            openDropDown.visibility = View.VISIBLE
        }

        // 북마크 버튼 → DB 저장 + 아이콘 변경
        dropdownBookmark.setOnClickListener {
            val recipe = currentRecipe
            if (recipe == null) {
                Toast.makeText(requireContext(), "레시피 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 중복 여부 검사
            if (dbHelper.isRecipeBookmarked(recipe.nameOfDish)) {
                Toast.makeText(requireContext(), "이미 저장된 레시피입니다.", Toast.LENGTH_SHORT).show()
            } else {
                // ★ Named arguments 제거!
                // insertSavedRecipe( recipeName, cookingTime, ingredients, recipeText, calorie, nutrient, saveDate )
                val insertedId = dbHelper.insertSavedRecipe(
                    recipe.nameOfDish,
                    recipe.cookingTime,
                    recipe.ingredients,
                    recipe.recipe,
                    recipe.calorie,
                    recipe.nutrient,
                    getCurrentDate() // 날짜
                )
                if (insertedId != -1L) {
                    Toast.makeText(requireContext(), "저장되었습니다", Toast.LENGTH_SHORT).show()
                    // 아이콘 채워진 북마크로
                    dropdownBookmark.setImageResource(R.drawable.dropdown_bookmark_filled)
                } else {
                    Toast.makeText(requireContext(), "DB 저장 실패!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dropdownTimer.setOnClickListener {
            val recipe = currentRecipe
            if (recipe == null) {
                Toast.makeText(requireContext(), "레시피 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cookingTimeStr = recipe.cookingTime.replace("[^\\d]".toRegex(), "")
            val cookingTimeMinutes = cookingTimeStr.toLongOrNull() ?: 15L
            val cookingTimeMillis = cookingTimeMinutes * 60 * 1000

            val intent = Intent(requireContext(), TimerActivity::class.java)
            intent.putExtra("timeInMillis", cookingTimeMillis)
            startActivity(intent)
        }

        // 알레르기 목록 (예시)
        val allergiesList = dbHelper.getAllAllergies()
        val allergiesData = allergiesList.joinToString(", ")

        // API 요청 (Named Argument 제거)
        requestRecipe(selectedCuisine, selectedCookingWay, selectedTime, selectedIngredients, allergiesData)
    }

    /**
     * Retrofit API 요청
     */
    private fun requestRecipe(
        cuisine: String,
        cookingWay: String,
        cookingTime: String,
        ingredients: String,
        allergies: String
    ) {
        // RecipeReqData(...)도 Named Args 없이 순서대로
        val request = RecipeReqData(
            allergies,
            cuisine,
            ingredients,
            cookingWay,
            cookingTime
        )

        showToast("레시피 요청 중...")
        service.askRecipe(request).enqueue(object : Callback<RecipeResData> {
            override fun onResponse(call: Call<RecipeResData>, response: Response<RecipeResData>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        currentRecipe = it
                        setRecipeData(it)
                    } ?: showToast("응답 데이터가 없습니다.")
                } else {
                    Log.e("API_ERROR", "Response failed: ${response.errorBody()?.string()}")
                    showToast("요청에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<RecipeResData>, t: Throwable) {
                Log.e("API_ERROR", "Network request failed", t)
                showToast("네트워크 오류가 발생했습니다.")
            }
        })
    }

    /**
     * UI에 레시피 데이터 반영
     */
    private fun setRecipeData(recipe: RecipeResData) {
        // 제목
        binding.editTextSearch.apply {
            setText(recipe.nameOfDish)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(null, Typeface.BOLD)
            setPadding(16, 0, 0, 0)
        }

        // 시간
        binding.textView20m.text = recipe.cookingTime

        // 재료
        val ingLayout = binding.linearLayoutScrollViewRecipeDetailIngredients
        ingLayout.removeAllViews()
        ingLayout.setPadding(30, 20, 30, 20)
        val ingText = TextView(requireContext()).apply {
            text = recipe.ingredients
            textSize = 15f
        }
        ingLayout.addView(ingText)

        // 조리방법
        val cookLayout = binding.linearLayoutScrollViewRecipeDetailCook
        cookLayout.removeAllViews()
        cookLayout.setPadding(30, 20, 30, 20)

        val steps = recipe.recipe.split(Regex("(?=\\d+\\.)"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (steps.isEmpty()) {
            val rText = TextView(requireContext()).apply {
                text = recipe.recipe
                textSize = 15f
            }
            cookLayout.addView(rText)
        } else {
            for (step in steps) {
                val stepText = TextView(requireContext()).apply {
                    text = step
                    textSize = 15f
                    setPadding(0, 5, 0, 5)
                }
                cookLayout.addView(stepText)
            }
        }

        // 칼로리
        val calLayout = binding.linearLayoutScrollViewCalorie
        calLayout.removeAllViews()
        calLayout.setPadding(30, 20, 30, 20)
        val calText = TextView(requireContext()).apply {
            text = recipe.calorie
            textSize = 15f
        }
        calLayout.addView(calText)

        // 영양성분
        val nutLayout = binding.linearLayoutScrollViewNutrients
        nutLayout.removeAllViews()
        nutLayout.setPadding(30, 20, 30, 20)
        val nutText = TextView(requireContext()).apply {
            text = recipe.nutrient
            textSize = 15f
        }
        nutLayout.addView(nutText)
    }

    /**
     * 현재 날짜(yyyy-MM-dd)
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
