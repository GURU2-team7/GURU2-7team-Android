package com.example.guru2.recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.guru2.MainActivity
import com.example.guru2.databinding.FragmentBookmarkBinding
import com.example.guru2.db.DatabaseHelper_recipe


class BookmarkFragment : Fragment() {
    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        // 데이터베이스 헬퍼 객체 생성(여기 26줄부터 -39줄 추가)
        val dbHelper = DatabaseHelper_recipe(requireContext())  // ❗ requireContext() 사용

        // 1️⃣ 레시피 추가
        val recipeId = dbHelper.addRecipe("김치찌개") // "김치찌개" 추가

        // 2️⃣ 레시피 상세 정보 추가
        if (recipeId != -1L) { // recipeId가 유효한 경우에만 추가
            dbHelper.addRecipeDetails(
                recipeId, 30,
                "돼지고기와 김치를 볶고 물을 넣어 끓인다.",
                500, "비타민C, 단백질"
            )
        }

        // ImageView 클릭 리스너 설정
        val backArrowButton = binding.backArrow  // FragmentBookmarkBinding을 통해 ImageView 찾기
        backArrowButton.setOnClickListener {
            // 메인 화면으로 돌아가는 Intent 생성
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)  // MainActivity로 이동
            activity?.finish()  // 현재 Activity 종료
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
