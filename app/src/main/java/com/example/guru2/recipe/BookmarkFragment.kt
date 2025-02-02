package com.example.guru2.recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        dbHelper = DatabaseHelper(requireContext())

        // 뒤로가기 아이콘 → MainActivity로
        val backArrowButton = binding.backArrow
        backArrowButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // DB에서 저장된 레시피 목록 로드
        loadSavedRecipes()

        return binding.root
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.rootlayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * DB에서 SavedRecipes 목록을 읽어 목록 표시
     */
    private fun loadSavedRecipes() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """SELECT ${DatabaseHelper.COLUMN_SAVED_ID}, 
                      ${DatabaseHelper.COLUMN_SAVED_NAME}, 
                      ${DatabaseHelper.COLUMN_SAVED_TIME}, 
                      ${DatabaseHelper.COLUMN_SAVED_DATE}
               FROM ${DatabaseHelper.TABLE_SAVED_RECIPES}
               ORDER BY ${DatabaseHelper.COLUMN_SAVED_DATE} DESC
            """.trimMargin(),
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val recipeId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAVED_ID))
                val recipeName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAVED_NAME))
                val cookingTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAVED_TIME))
                val saveDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAVED_DATE))

                // item_recipe.xml inflate
                val itemBinding = ItemRecipeBinding.inflate(layoutInflater, binding.linearLayoutList, false)

                // 간단한 정보 바인딩
                itemBinding.textViewRecipeName.text = recipeName
                itemBinding.textViewDate.text = saveDate
                itemBinding.cookingHour.text = "${cookingTime}"

                // 클릭 시 → RecipeDetailFragment (ID만 넘김)
                itemBinding.root.setOnClickListener {
                    val fragment = RecipeDetailFragment().apply {
                        arguments = Bundle().apply {
                            putInt("RECIPE_ID", recipeId)
                        }
                    }
                    navigateToFragment(fragment)
                }

                binding.linearLayoutList.addView(itemBinding.root)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(requireContext(), "저장된 레시피가 없습니다.", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}
