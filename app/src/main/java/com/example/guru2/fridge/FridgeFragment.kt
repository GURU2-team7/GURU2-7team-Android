package com.example.guru2.fridge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guru2.MainActivity
import com.example.guru2.databinding.FragmentFridgeBinding
import com.example.guru2.databinding.ItemIngredientBinding
import com.example.guru2.db.DatabaseHelper
import com.example.guru2.recipe.RecipeFragment
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class FridgeFragment : Fragment() {

    private var _binding: FragmentFridgeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private val ingredientList = mutableListOf<Ingredient>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFridgeBinding.inflate(inflater, container, false)

        // DatabaseHelper 초기화
        dbHelper = DatabaseHelper(requireContext())

        // DB에서 기존 데이터를 불러와 UI에 반영
        ingredientList.addAll(dbHelper.getAllFridgeItems())
        refreshList(binding.linearLayoutList, ingredientList)

        // 뒤로가기 아이콘 → MainActivity로
        val backArrowButton = binding.backArrow
        backArrowButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
            // 또는 parentFragmentManager.popBackStack() 등 다른 방법 가능


        // 버튼 동작 설정
        setupButtons()

        return binding.root
    }

    private fun setupButtons() {
        // "⊕" 버튼 => 재료 추가
        binding.buttonAddItem.setOnClickListener {
            val inputText = binding.editTextSearch.text.toString().trim()
            if (inputText.isNotEmpty()) {
                // 이미 등록된 재료인지 확인
                if (ingredientList.any { it.name.equals(inputText, ignoreCase = true) }) {
                    Toast.makeText(requireContext(), "이미 등록된 재료입니다.", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext(), "재료를 추가하는 데 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "재료 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
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
    }

    /**
     * 재료 목록을 LinearLayout에 순서대로 추가 (UI 갱신)
     */
    private fun refreshList(parent: LinearLayout, list: List<Ingredient>) {
        parent.removeAllViews()
        for (item in list) {
            addItemView(parent, item)
        }
    }

    /**
     * 한 개의 재료(Ingredient)에 대한 View를 생성하여 parent에 추가
     */
    private fun addItemView(parent: LinearLayout, ingredient: Ingredient) {
        val itemBinding =
            ItemIngredientBinding.inflate(LayoutInflater.from(requireContext()), parent, false)

        // 재료 이름 15자 초과 시 "..."로 생략
        val truncatedName = if (ingredient.name.length > 15) {
            ingredient.name.take(15) + "..."
        } else {
            ingredient.name
        }

        itemBinding.textViewIngredientName.text = truncatedName
        itemBinding.textViewDate.text = ingredient.date
        itemBinding.textViewQuantity.text = ingredient.quantity.toString()

        // 삭제 버튼
        itemBinding.buttonDeleteItem.setOnClickListener {
            // DB에서 삭제
            dbHelper.deleteFromFridge(ingredient.name)
            // UI에서 제거
            parent.removeView(itemBinding.root)
            ingredientList.remove(ingredient)
        }

        // 수량 감소 버튼
        itemBinding.buttonDecreaseQuantity.setOnClickListener {
            if (ingredient.quantity > 1) {
                ingredient.quantity--
                itemBinding.textViewQuantity.text = ingredient.quantity.toString()
                // DB 업데이트
                dbHelper.updateFridgeQuantity(ingredient.name, ingredient.quantity)
            }
        }

        // 수량 증가 버튼
        itemBinding.buttonIncreaseQuantity.setOnClickListener {
            ingredient.quantity++
            itemBinding.textViewQuantity.text = ingredient.quantity.toString()
            // DB 업데이트
            dbHelper.updateFridgeQuantity(ingredient.name, ingredient.quantity)
        }

        // layout에 최종 추가
        parent.addView(itemBinding.root)
    }

    /**
     * 현재 날짜(yyyy-MM-dd) 반환
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //여기 추가 레시피 재료 데이터 recipe 요청 페이지로 전달
    private fun openRecipeFragment() {
        val ingredients = ingredientList.map { it.name }.toTypedArray() // 재료 이름만 배열로 변환
        val bundle = Bundle().apply {
            putStringArray("ingredientList", ingredients) // Bundle에 저장
        }

        val recipeFragment = RecipeFragment().apply {
            arguments = bundle // RecipeFragment에 데이터 전달
        }

    }
}
