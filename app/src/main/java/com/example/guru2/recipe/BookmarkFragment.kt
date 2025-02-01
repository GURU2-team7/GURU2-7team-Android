package com.example.guru2.recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guru2.MainActivity
import com.example.guru2.R
import com.example.guru2.databinding.FragmentBookmarkBinding
import com.example.guru2.databinding.ItemRecipeBinding
import com.example.guru2.db.DatabaseHelper

class BookmarkFragment : Fragment() {
    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper  // DB Helper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        dbHelper = DatabaseHelper(requireContext())

        // "레시피 상세 보기" 버튼 클릭 시 RecipeDetailFragment로 이동
        binding.buttonRecipeDetail.setOnClickListener {
            navigateToFragment(RecipeDetailFragment())
        }

        // ImageView 클릭 리스너 설정
        val backArrowButton = binding.backArrow  // FragmentBookmarkBinding을 통해 ImageView 찾기
        backArrowButton.setOnClickListener {
            // 메인 화면으로 돌아가는 Intent 생성
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)  // MainActivity로 이동
            activity?.finish()  // 현재 Activity 종료
        }

        // 레시피 데이터를 가져와서 동적으로 추가
        loadSavedRecipes()

        return binding.root
    }
    private fun navigateToFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.rootlayout, fragment)
        transaction.addToBackStack(null)  // 뒤로 가기 가능하도록 백스택 추가
        transaction.commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // DB에서 저장된 레시피 데이터를 가져와 동적으로 추가하는 함수
    private fun loadSavedRecipes() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """SELECT ${DatabaseHelper.COLUMN_SAVED_ID}, ${DatabaseHelper.COLUMN_SAVED_NAME}, 
                      ${DatabaseHelper.COLUMN_SAVED_TIME}, ${DatabaseHelper.COLUMN_SAVED_DATE} 
               FROM ${DatabaseHelper.TABLE_SAVED_RECIPES}
               ORDER BY ${DatabaseHelper.COLUMN_SAVED_DATE} DESC""", null
        )

        if (cursor.moveToFirst()) {
            do {
                val recipeId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAVED_ID))
                val recipeName =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAVED_NAME))
                val cookingTime =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAVED_TIME))
                val saveDate =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAVED_DATE))

                // item_recipe.xml을 inflate (ViewBinding 사용)
                val itemBinding =
                    ItemRecipeBinding.inflate(layoutInflater, binding.linearLayoutList, false)

                // 레시피 정보를 바인딩
                itemBinding.textViewRecipeName.text = recipeName
                itemBinding.textViewDate.text = saveDate
                itemBinding.cookingHour.text = "${cookingTime}M"

                // 레시피 클릭 시 상세 페이지로 이동 (Fragment로 이동)
                itemBinding.root.setOnClickListener {
                    val fragment = RecipeDetailFragment().apply {
                        arguments = Bundle().apply {
                            putInt("RECIPE_ID", recipeId)
                        }
                    }
                    navigateToFragment(fragment)
                }

                // LinearLayout에 아이템 추가
                binding.linearLayoutList.addView(itemBinding.root)

            } while (cursor.moveToNext())
        } else {
            Toast.makeText(requireContext(), "저장된 레시피가 없습니다.", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
    }
}