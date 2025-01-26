package com.example.guru2.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.guru2.allergy.AllergyActivity
import com.example.guru2.databinding.FragmentHomeBinding
import com.example.guru2.fridge.FridgeActivity
import com.example.guru2.recipe.RecipeActivity
import com.example.guru2.recipe.recipe1

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // AllergyActivity로 이동 버튼 이벤트
        binding.buttonGoToAllergy.setOnClickListener {
            val intent = Intent(requireContext(), AllergyActivity::class.java)
            startActivity(intent)
        }

        // FridgeActivity로 이동 버튼 이벤트
        binding.buttonGoToFridge.setOnClickListener {
            val intent = Intent(requireContext(), FridgeActivity::class.java)
            startActivity(intent)
        }

        // 레시피 요청 페이지로 이동 버튼
        binding.buttonGoToAskRecipe.setOnClickListener {
            val intent = Intent(requireContext(), RecipeActivity::class.java)
            startActivity(intent)
        }

        // 레시피 요청 페이지로 이동 버튼 (다른 액티비티)
        binding.buttonGoToAskRecipe1.setOnClickListener {
            val intent = Intent(requireContext(), recipe1::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
