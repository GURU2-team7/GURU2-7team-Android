package com.example.guru2.recipe

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        binding.btnAskRecipe.setOnClickListener {
            requestRecipe()
        }
    }

    private fun requestRecipe() {

        // 사용자 요청 데이터 저장
        val request = RecipeReqData(
            binding.allergyEdt.text.toString(),
            binding.typeOfCookingEdt.text.toString(),
            binding.ingredientsEdt.text.toString(),
            binding.cookingMethodEdt.text.toString(),
            binding.cookingTimeEdt.text.toString()
        )

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
