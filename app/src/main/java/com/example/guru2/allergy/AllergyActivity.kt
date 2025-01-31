package com.example.guru2.allergy

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.example.guru2.databinding.ActivityAllergyBinding
import com.example.guru2.db.DatabaseHelper
import com.google.android.material.chip.Chip

class AllergyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllergyBinding

    // DB 연동을 위한 Helper
    private lateinit var dbHelper: DatabaseHelper

    // 기본 제공 리스트들
    private val dairyList = listOf("우유", "밀")
    private val nutsList = listOf("메밀", "땅콩", "대두", "호두")
    private val seafoodList = listOf("오징어", "조개류 (굴, 전복, 홍합 등)", "고등어", "게", "새우")
    private val meatList = listOf("돼지고기", "닭고기", "쇠고기")
    private val fruitVegList = listOf("토마토", "아황산류", "복숭아")

    // 현재 화면에서 선택된 알레르기 목록 (UI + DB 둘 다 반영)
    private val selectedAllergies = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화
        binding = ActivityAllergyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) DatabaseHelper 초기화
        dbHelper = DatabaseHelper(this)

        // 2) DB에서 이미 저장된 알레르기 목록 불러오기 -> 화면에도 표시
        val savedList = dbHelper.getAllAllergies()
        selectedAllergies.addAll(savedList)
        for (allergy in savedList) {
            addChip(allergy)
        }

        // 3) 카테고리별 리스트뷰 연결
        setupListView(binding.toggleDairy, binding.listViewDairy, dairyList)
        setupListView(binding.toggleNuts, binding.listViewNuts, nutsList)
        setupListView(binding.toggleSeafood, binding.listViewSeafood, seafoodList)
        setupListView(binding.toggleMeat, binding.listViewMeat, meatList)
        setupListView(binding.toggleFruitVeg, binding.listViewFruitVeg, fruitVegList)

        // 4) 기타 항목 등록 버튼
        binding.buttonRegister.setOnClickListener {
            val etcText = binding.editTextEtc.text.toString().trim()
            if (etcText.isNotEmpty()) {
                if (selectedAllergies.contains(etcText)) {
                    Toast.makeText(this, "이미 등록된 항목입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 메모리에 저장
                    selectedAllergies.add(etcText)
                    // DB에 저장
                    dbHelper.insertAllergy(etcText)
                    // 화면에 Chip 추가
                    addChip(etcText)
                    binding.editTextEtc.text.clear()
                }
            }
        }

        // 5) 저장 버튼
        binding.buttonSave.setOnClickListener {
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 6) 뒤로가기 버튼
        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    /**
     * ListView 설정 함수
     * - 파라미터 listView를 명시적으로 ListView 타입으로 받는다.
     * - 토글을 누르면 ListView를 열고 닫는다.
     * - 클릭 이벤트는 setOnItemClickListener를 통해 처리한다.
     */
    private fun setupListView(toggle: View, listView: ListView, items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        // 토글 열고 닫기
        toggle.setOnClickListener {
            if (listView.visibility == View.GONE) {
                listView.visibility = View.VISIBLE
                (toggle as? ImageView)?.setImageResource(R.drawable.toggle_open)
                listView.post {
                    setListViewHeightBasedOnChildren(listView)
                }
            } else {
                listView.visibility = View.GONE
                (toggle as? ImageView)?.setImageResource(R.drawable.toggle_closed)
            }
        }

        // 항목 클릭 이벤트
        listView.setOnItemClickListener { _, _, position: Int, _ ->
            val selectedItem = items[position]
            if (selectedAllergies.contains(selectedItem)) {
                Toast.makeText(this, "이미 등록된 항목입니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 메모리에 추가
                selectedAllergies.add(selectedItem)
                // DB에 저장
                dbHelper.insertAllergy(selectedItem)
                // 화면에 Chip 추가
                addChip(selectedItem)
            }
        }
    }

    /**
     * Chip(태그) 추가 함수
     * - X 아이콘을 누르면 리스트와 DB에서 모두 제거
     */
    private fun addChip(text: String) {
        val chip = Chip(this).apply {
            this.text = text
            isCloseIconVisible = true
            // Chip 디자인
            chipBackgroundColor = getColorStateList(R.color.white)
            chipStrokeColor = getColorStateList(R.color.light_gray)
            chipStrokeWidth = 2f

            setOnCloseIconClickListener {
                binding.chipGroupAllergy.removeView(this)
                selectedAllergies.remove(text)
                // DB에서도 제거
                dbHelper.deleteAllergy(text)
            }
        }
        binding.chipGroupAllergy.addView(chip)
    }

    /**
     * ScrollView 안 ListView 높이 동적 설정
     * - ListView를 wrap_content처럼 동작하게 해 주는 일종의 트릭
     */
    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        // dividerHeight가 있을 경우 모두 합산
        val dividerHeight = listView.dividerHeight * (listAdapter.count - 1)
        params.height = totalHeight + dividerHeight
        listView.layoutParams = params
        listView.requestLayout()
    }
}
