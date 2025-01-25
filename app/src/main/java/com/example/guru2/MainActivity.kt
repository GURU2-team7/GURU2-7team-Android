package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import com.example.guru2.databinding.ActivityMainBinding // 뷰 바인딩 클래스 임포트
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // 뷰 바인딩 객체 선언
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 초기 프래그먼트 설정
        val naviHomeFragment =
            Navi_HomeFragment()
        val naviCookFragment =
            Navi_CookFragment()
        val naviFrigerFragment =
            Navi_FrigerFragment()
        val naviSaveFragment =
            Navi_SaveFragment()

        // 기본적으로 homeFragment를 화면에 표시
        supportFragmentManager.beginTransaction()
            .add(R.id.rootlayout, naviHomeFragment)
            .commit()

        // BottomNavigationView 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()

            when (item.itemId) {
                R.id.tabHome -> {
                    fragmentTransaction.replace(R.id.rootlayout, naviHomeFragment).commit()
                    true
                }
                R.id.tabCook -> {
                    fragmentTransaction.replace(R.id.rootlayout, naviCookFragment).commit()
                    true
                }
                R.id.tabFriger -> {
                    fragmentTransaction.replace(R.id.rootlayout, naviFrigerFragment).commit()
                    true
                }
                R.id.tabSave -> {
                    fragmentTransaction.replace(R.id.rootlayout, naviSaveFragment).commit()
                    true
                }
                else -> false
            }
        }

        // 뷰 바인딩 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-Edge 설정 유지
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // AllergyActivity로 이동 버튼 이벤트
        binding.buttonGoToAllergy.setOnClickListener {
            val intent = Intent(this, AllergyActivity::class.java)
            startActivity(intent)
        }

        // FridgeActivity로 이동 버튼 이벤트
        binding.buttonGoToFridge.setOnClickListener {
            val intent = Intent(this, FridgeActivity::class.java)
            startActivity(intent)
        }

        // 레시피 요청 페이지로 이동 버튼 - sample
        val buttonGoToAskRecipe: Button = findViewById(R.id.buttonGoToAskRecipe)
        binding.buttonGoToAskRecipe.setOnClickListener {
            val intent = Intent(this, AskRecipeActivity::class.java)
            startActivity(intent)
        }
    }
}
