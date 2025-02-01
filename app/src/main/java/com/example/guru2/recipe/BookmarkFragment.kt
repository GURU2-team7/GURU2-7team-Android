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
        loadRecipeData()

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

    // DB에서 레시피 데이터를 가져오고 LinearLayout에 동적으로 추가
    private fun loadRecipeData() {
        val db = dbHelper.readableDatabase

        // 테이블을 JOIN하여 레시피 정보와 쿠킹타임을 가져오는 쿼리
        val cursor = db.rawQuery("""
            SELECT r.${DatabaseHelper.COLUMN_RECIPE_ID}, r.${DatabaseHelper.COLUMN_RECIPE_NAME}, r.${DatabaseHelper.COLUMN_RECIPE_ADDED_DATE}, 
                   d.${DatabaseHelper.COLUMN_RECIPE_COOKINGTIME}
            FROM ${DatabaseHelper.TABLE_RECIPE} r
            LEFT JOIN ${DatabaseHelper.TABLE_RECIPE_DETAILS} d 
            ON r.${DatabaseHelper.COLUMN_RECIPE_ID} = d.${DatabaseHelper.COLUMN_RECIPE_ID}
        """, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val recipeId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECIPE_ID))
                val recipeName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECIPE_NAME))
                val addedDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECIPE_ADDED_DATE))
                val cookingTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECIPE_COOKINGTIME))

                // item_recipe.xml을 동적으로 inflate
                val itemView = layoutInflater.inflate(R.layout.item_recipe, binding.linearLayoutList, false)

                // 레시피 정보를 itemView에 바인딩
                val recipeNameTextView: TextView = itemView.findViewById(R.id.textViewRecipeName)
                val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
                val cookingTimeTextView: TextView = itemView.findViewById(R.id.cookingHour)

                // 레시피 이름, 등록 날짜, 쿠킹타임을 설정
                recipeNameTextView.text = recipeName
                dateTextView.text = addedDate
                cookingTimeTextView.text = "${cookingTime}M"  // "30M" 형식으로 표시

                // 레시피 클릭 시 상세 페이지로 이동 ==> 여기 수정해야함!!!!!
//                itemView.setOnClickListener {
//                    val intent = Intent(requireContext(), RecipedetailFragment::class.java)
//                    intent.putExtra("RECIPE_ID", recipeId)  // 클릭한 레시피의 ID 전달
//                    startActivity(intent)
//                }

                // LinearLayout에 itemView 추가
                binding.linearLayoutList.addView(itemView)

            } while (cursor.moveToNext())
        } else {
            Toast.makeText(requireContext(), "레시피를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
        cursor?.close()
    }
}