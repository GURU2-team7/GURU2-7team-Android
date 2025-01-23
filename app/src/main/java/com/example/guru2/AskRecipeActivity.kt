package com.example.guru2

import android.os.Bundle
import android.text.style.TtsSpan.TextBuilder
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guru2.api.RecipeReqData
import com.example.guru2.api.RecipeResData
import com.example.guru2.api.RetrofitClient
import com.example.guru2.api.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AskRecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ask_recipe_sample)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val allergyEdt: EditText = findViewById(R.id.allergyEdt)
        val typeOfCookingEdt: EditText = findViewById(R.id.typeOfCookingEdt)
        val ingredientEdt: EditText = findViewById(R.id.ingredientEdt)
        val cookingMethodEdt: EditText = findViewById(R.id.cookingMethodEdt)
        val cookingTimeEdt: EditText = findViewById(R.id.cookingTimeEdt)

        val nameOfDishTv: TextView = findViewById(R.id.nameOfDishTv)
        val ingredientsTv: TextView = findViewById(R.id.ingredientsTv)
        val recipeTv: TextView = findViewById(R.id.recipeTv)
        val calorieTv: TextView = findViewById(R.id.calorieTv)
        val nutrientTv: TextView = findViewById(R.id.nutrientTv)
        val cookingTimeTv: TextView = findViewById(R.id.cookingTimeTv)

        // 레시피 api 요청
        val service = RetrofitClient.getClient().create(RetrofitService::class.java)
        val btnAskRecipe: Button = findViewById(R.id.btnAskRecipe)

        btnAskRecipe.setOnClickListener {
            val request = RecipeReqData(
                allergyEdt.text.toString(),
                typeOfCookingEdt.text.toString(),
                ingredientEdt.text.toString(),
                cookingMethodEdt.text.toString(),
                cookingTimeEdt.text.toString()
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
                            nameOfDishTv.text = nameOfDish
                            ingredientsTv.text = ingredients
                            recipeTv.text = recipe
                            calorieTv.text = calorie
                            nutrientTv.text = nutrient
                            cookingTimeTv.text = cookingTime
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