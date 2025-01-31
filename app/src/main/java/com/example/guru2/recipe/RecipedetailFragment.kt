package com.example.guru2.recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.guru2.MainActivity
import com.example.guru2.R
import com.example.guru2.databinding.FragmentRecipedetailBinding

class RecipedetailFragment : Fragment() {
    private var _binding: FragmentRecipedetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipedetailBinding.inflate(inflater, container, false)

        // ImageView 클릭 리스너 설정
        val backArrowButton1 =
            binding.backArrowToBookmark  // FragmentBookmarkBinding을 통해 ImageView 찾기
        backArrowButton1.setOnClickListener {
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
