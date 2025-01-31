package com.example.guru2.recipe

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.example.guru2.api.RecipeReqData
import com.example.guru2.api.RecipeResData
import com.example.guru2.api.RetrofitClient
import com.example.guru2.api.RetrofitService
import com.example.guru2.databinding.ActivityAskRecipeSampleBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AskRecipeActivity : AppCompatActivity() {
    // binding 객체 선언
    private lateinit var binding: ActivityAskRecipeSampleBinding

    private val service: RetrofitService by lazy {
        RetrofitClient.getClient().create(RetrofitService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // viewBinding 적용
        binding = ActivityAskRecipeSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecipeFragment에서 전달된 값 받기
        val selectedCuisine = intent.getStringExtra("cuisine") ?: "없음"
        val selectedCookingWay = intent.getStringExtra("cookingWay") ?: "없음"
        val selectedTime = intent.getStringExtra("time") ?: "없음"
        val selectedIngredients = intent.getStringExtra("ingredients") ?: "없음"

        // 전달된 값 로그로 확인
        Log.d("AskRecipeActivity", "선택된 요리: $selectedCuisine, 방식: $selectedCookingWay, 시간: $selectedTime, 재료: $selectedIngredients")

        // 필요한 값을 화면에 표시하거나 처리하는 부분 추가 가능
        // 예: binding.selectedCuisineTv.text = selectedCuisine

        // 각 EditText에 값 채우기
        binding.ingredientsEdt.setText(selectedIngredients)
        binding.typeOfCookingEdt.setText(selectedCuisine)
        binding.cookingMethodEdt.setText(selectedCookingWay)
        binding.cookingTimeEdt.setText(selectedTime)

        // 버튼 클릭 시 레시피 요청
        binding.btnAskRecipe.setOnClickListener {
            requestRecipe(selectedCuisine, selectedCookingWay, selectedTime, selectedIngredients)
        }
    }

    private fun requestRecipe(cuisine: String, way: String, time: String, ingredients : String) {

        //사용자 요청
        val request = RecipeReqData(
            binding.allergyEdt.text.toString(),  // 알레르기 정보 (EditText에서 가져오기)
            cuisine,  // 요리 종류
            ingredients,  // 선택된 재료
            way,  // 요리 방식
            time  // 요리 시간
        )

        Toast.makeText(this, "레시피 요청을 보냈습니다.", Toast.LENGTH_SHORT).show()

        // 요청 로그 출력
        Log.d("AskRecipeActivity", "요청 데이터: $request")

        // recipe api 요청

        // enqueue 를 통해 비동기 요청을 보냄 (앱이 정지되지 않도록)
        service.askRecipe(request).enqueue(object : Callback<RecipeResData> {
            // 응답이 오면 실행
            override fun onResponse(call: Call<RecipeResData>, response: Response<RecipeResData>) {
                // 요청 성공이면
                if (response.isSuccessful) {
                    response.body()?.let {
                        setRecipeData(it)
                    }
                        ?: showToast("응답 데이터가 없습니다.")
                } else { // 요청 실패면
                    Log.e("API_ERROR", "Response failed: ${response.errorBody()?.string()}")
                    showToast("요청에 실패했습니다.")
                }
            }

            // 응답이 오지 않으면 실행
            override fun onFailure(call: Call<RecipeResData>, t: Throwable) {
                Log.e("API_ERROR", "Network request failed", t)
                showToast("네트워크 오류가 발생했습니다.")
            }
        })
    }

    // 화면에 응답 데이터 반영
    private fun setRecipeData(recipe: RecipeResData) {
        binding.apply {
            nameOfDishTv.text = recipe.nameOfDish
            ingredientsTv.text = recipe.ingredients
            recipeTv.text = recipe.recipe
            calorieTv.text = recipe.calorie
            nutrientTv.text = recipe.nutrient
            cookingTimeTv.text = recipe.cookingTime
        }
    }

    // 오류 메시지 출력
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
