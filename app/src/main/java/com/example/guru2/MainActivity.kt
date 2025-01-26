package com.example.guru2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.guru2.databinding.ActivityMainBinding
import com.example.guru2.fridge.FridgeFragment
import com.example.guru2.home.HomeFragment
import com.example.guru2.recipe.BookmarkFragment
import com.example.guru2.recipe.RecipeFragment

class MainActivity : AppCompatActivity(), HomeFragment.OnFragmentChangeListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본적으로 HomeFragment 표시
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // BottomNavigationView 설정
        val bottomNavigationView = binding.bottomNav
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tabHome -> replaceFragment(HomeFragment())
                R.id.tabRecipe -> replaceFragment(RecipeFragment())
                R.id.tabFridge -> replaceFragment(FridgeFragment())
                R.id.tabBookmark -> replaceFragment(BookmarkFragment())
            }
            true
        }
    }

    // Fragment 변경 함수 (중복 방지)
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.rootlayout)

        if (currentFragment != null && currentFragment::class == fragment::class) {
            return
        }

        fragmentManager.beginTransaction()
            .replace(R.id.rootlayout, fragment)
            .commitAllowingStateLoss()
    }

    // HomeFragment에서 호출할 인터페이스 구현
    override fun onChangeFragment(fragment: Fragment) {
        replaceFragment(fragment)
    }
}
