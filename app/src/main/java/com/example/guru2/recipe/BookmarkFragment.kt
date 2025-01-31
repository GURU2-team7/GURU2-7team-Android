package com.example.guru2.recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.guru2.MainActivity
import com.example.guru2.R
import com.example.guru2.databinding.FragmentBookmarkBinding

class BookmarkFragment : Fragment() {
    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)

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
}