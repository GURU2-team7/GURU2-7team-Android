package com.example.guru2

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.databinding.ActivityFridgeBinding
import com.example.guru2.databinding.ItemIngredientBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class FridgeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFridgeBinding
    private lateinit var dbHelper: DatabaseHelper // SQLite 데이터베이스 헬퍼 클래스
    private val ingredientList = mutableListOf<Ingredient>() // 전체 재료 목록

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivityFridgeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DatabaseHelper 초기화
        dbHelper = DatabaseHelper(this)

        // DB에서 기존 데이터를 로드하고 UI에 반영
        ingredientList.addAll(dbHelper.getAllFridgeItems())
        refreshList(binding.linearLayoutList, ingredientList)

        // 뒤로가기 버튼
        binding.backArrow.setOnClickListener {
            finish()
        }

        // "⊕" 버튼 => 재료 추가
        binding.buttonAddItem.setOnClickListener {
            val inputText = binding.editTextSearch.text.toString().trim()
            if (inputText.isNotEmpty()) {
                if (ingredientList.any { it.name.equals(inputText, ignoreCase = true) }) {
                    Toast.makeText(this, "이미 등록된 재료입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    val currentDate = getCurrentDate()
                    val ingredient = Ingredient(name = inputText, date = currentDate, quantity = 1)

                    // DB에 추가
                    val ingredientId = dbHelper.insertIngredient(inputText)
                    if (ingredientId != -1L) {
                        dbHelper.addToFridge(inputText, "1")

                        // UI 업데이트
                        ingredientList.add(ingredient)
                        addItemView(binding.linearLayoutList, ingredient)
                        binding.editTextSearch.text.clear()
                    } else {
                        Toast.makeText(this, "재료를 추가하는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "재료 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 돋보기 버튼 => 검색
        binding.buttonSearch.setOnClickListener {
            val query = binding.editTextSearch.text.toString().trim()
            val filteredList = if (query.isNotEmpty()) {
                dbHelper.searchFridgeItems(query)
            } else {
                dbHelper.getAllFridgeItems()
            }
            refreshList(binding.linearLayoutList, filteredList)
        }

        // 네비게이션 바 아이콘 클릭 이벤트 설정
        binding.navHome.setOnClickListener {
            Toast.makeText(this, "홈 화면으로 이동 (미구현)", Toast.LENGTH_SHORT).show()
        }
        binding.navRecipeRequest.setOnClickListener {
            Toast.makeText(this, "레시피 요청 페이지 (미구현)", Toast.LENGTH_SHORT).show()
        }
        binding.navFridge.setOnClickListener {
            Toast.makeText(this, "이미 냉장고 페이지입니다.", Toast.LENGTH_SHORT).show()
        }
        binding.navSavedRecipes.setOnClickListener {
            Toast.makeText(this, "레시피 저장 페이지 (미구현)", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * item_ingredient.xml을 inflate하여 스크롤뷰 내부의 linearLayoutList에 추가
     */
    private fun addItemView(parent: LinearLayout, ingredient: Ingredient) {
        val itemBinding = ItemIngredientBinding.inflate(LayoutInflater.from(this), parent, false)

        // 데이터 반영
        itemBinding.textViewIngredientName.text = ingredient.name
        itemBinding.textViewDate.text = ingredient.date
        itemBinding.textViewQuantity.text = ingredient.quantity.toString()

        // 삭제 버튼
        itemBinding.buttonDeleteItem.setOnClickListener {
            // DB에서 삭제
            dbHelper.deleteFromFridge(ingredient.name)
            parent.removeView(itemBinding.root)
            ingredientList.remove(ingredient)
        }

        // 수량 감소
        itemBinding.buttonDecreaseQuantity.setOnClickListener {
            if (ingredient.quantity > 1) {
                ingredient.quantity--
                itemBinding.textViewQuantity.text = ingredient.quantity.toString()
                dbHelper.updateFridgeQuantity(ingredient.name, ingredient.quantity)
            }
        }

        // 수량 증가
        itemBinding.buttonIncreaseQuantity.setOnClickListener {
            ingredient.quantity++
            itemBinding.textViewQuantity.text = ingredient.quantity.toString()
            dbHelper.updateFridgeQuantity(ingredient.name, ingredient.quantity)
        }

        // parent에 최종 추가
        parent.addView(itemBinding.root)
    }

    /**
     * 주어진 리스트로 전체를 다시 그리는 함수
     */
    private fun refreshList(parent: LinearLayout, list: List<Ingredient>) {
        parent.removeAllViews()
        for (item in list) {
            addItemView(parent, item)
        }
    }

    /**
     * yyyy-MM-dd 형식의 오늘 날짜 문자열
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
