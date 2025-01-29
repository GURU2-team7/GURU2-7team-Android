package com.example.guru2.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 버튼 클릭 이벤트 설정
        binding.backArrowToBookmark.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // 뒤로 가기
        }

        // DrawerLayout을 열기 위한 버튼
        binding.openDropDown.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // 닫기 버튼
        binding.dropdownX.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        // 북마크 버튼
        binding.dropdownBookmark.setOnClickListener {
            // 추가 동작 가능
        }

        // 타이머 버튼
        binding.dropdownTimer.setOnClickListener {
            // 추가 동작 가능
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
