package com.example.guru2.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.guru2.R
import com.example.guru2.databinding.FragmentBookmarkBinding
import com.example.guru2.recipe.RecipedetailFragment

class BookmarkFragment : Fragment() {
    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        // RecipeDetailFragment로 이동하는 버튼 클릭 이벤트 설정
        binding.btnRecipeDetail.setOnClickListener {
            navigateToFragment(RecipedetailFragment()) // RecipeDetailFragment로 이동
        }


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
}
