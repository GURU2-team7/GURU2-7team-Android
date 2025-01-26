package com.example.guru2.fridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guru2.databinding.FragmentFridgeBinding
import com.example.guru2.databinding.ItemIngredientBinding
import com.example.guru2.db.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class FridgeFragment : Fragment() {

    private var _binding: FragmentFridgeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper // SQLite 데이터베이스 헬퍼 클래스
    private val ingredientList = mutableListOf<Ingredient>() // 전체 재료 목록

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFridgeBinding.inflate(inflater, container, false)

        // DatabaseHelper 초기화
        dbHelper = DatabaseHelper(requireContext())

        // DB에서 기존 데이터를 로드하고 UI에 반영
        ingredientList.addAll(dbHelper.getAllFridgeItems())
        refreshList(binding.linearLayoutList, ingredientList)

        // 버튼 동작 설정
        setupButtons()

        return binding.root
    }

    private fun setupButtons() {
        // "⊕" 버튼 => 재료 추가
        binding.buttonAddItem.setOnClickListener {
            val inputText = binding.editTextSearch.text.toString().trim()
            if (inputText.isNotEmpty()) {
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
                        Toast.makeText(requireContext(), "재료를 추가하는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
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

    private fun addItemView(parent: LinearLayout, ingredient: Ingredient) {
        val itemBinding = ItemIngredientBinding.inflate(LayoutInflater.from(requireContext()), parent, false)

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

    private fun refreshList(parent: LinearLayout, list: List<Ingredient>) {
        parent.removeAllViews()
        for (item in list) {
            addItemView(parent, item)
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
