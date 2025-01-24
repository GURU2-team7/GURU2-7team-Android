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

        // 레시피 api 요청
        val service = RetrofitClient.getClient().create(RetrofitService::class.java)
        val btnAskRecipe: Button = findViewById(R.id.btnAskRecipe)

        btnAskRecipe.setOnClickListener {
            val request = RecipeReqData(
                binding.allergyEdt.text.toString(),
                binding.typeOfCookingEdt.text.toString(),
                binding.ingredientsEdt.text.toString(),
                binding.cookingMethodEdt.text.toString(),
                binding.cookingTimeEdt.text.toString()
            )

            service.askRecipe(request).enqueue(object : Callback<RecipeResData> {
                override fun onResponse(call: Call<RecipeResData>, response: Response<RecipeResData>) {
                    if (response.isSuccessful) {
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
                    } else {
                        Log.e("API_ERROR", "Response failed: ${response.errorBody()?.string()}")
                        val toast = Toast.makeText(this@AskRecipeActivity, "모든 데이터를 응답받지 못했습니다.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<RecipeResData>, t: Throwable) {
                    Log.e("API_ERROR", "Network request failed", t)
                    Toast.makeText(this@AskRecipeActivity, "요청에 실패했습니다.", Toast.LENGTH_LONG).show()
                }
            })
        }

    }

}