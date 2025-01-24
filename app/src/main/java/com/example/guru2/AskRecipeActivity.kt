package com.example.guru2

import android.os.Bundle
import android.util.Log
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // viewBinding 적용
        binding = ActivityAskRecipeSampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val service = RetrofitClient.getClient().create(RetrofitService::class.java)

        binding.btnAskRecipe.setOnClickListener {
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
                        // 응답 본문 가져오기
                        val recipeResponse = response.body()

                        if (recipeResponse != null) {
                            // 응답 데이터를 변수에 저장
                            val nameOfDish = recipeResponse.nameOfDish
                            val ingredients = recipeResponse.ingredients
                            val recipe = recipeResponse.recipe
                            val calorie = recipeResponse.calorie
                            val nutrient = recipeResponse.nutrient
                            val cookingTime = recipeResponse.cookingTime

                            // 화면의 textview를 응답 데이터로 수정
                            binding.nameOfDishTv.text = nameOfDish
                            binding.ingredientsTv.text = ingredients
                            binding.recipeTv.text = recipe
                            binding.calorieTv.text = calorie
                            binding.nutrientTv.text = nutrient
                            binding.cookingTimeTv.text = cookingTime
                        }
                    } else { // 요청 실패인 경우
                        Log.e("API_ERROR", "Response failed: ${response.errorBody()?.string()}")
                        val toast = Toast.makeText(this@AskRecipeActivity, "요청에 실패했습니다.", Toast.LENGTH_LONG).show()
                    }
                }

                // 응답이 오지 않으면 실행
                override fun onFailure(call: Call<RecipeResData>, t: Throwable) {
                    Log.e("API_ERROR", "Network request failed", t)
                    Toast.makeText(this@AskRecipeActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}