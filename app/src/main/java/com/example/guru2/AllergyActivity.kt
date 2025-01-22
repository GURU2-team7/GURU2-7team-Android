package com.example.guru2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AllergyActivity : AppCompatActivity() {

    private lateinit var backArrow: ImageView
    private lateinit var toggleDairy: ImageView
    private lateinit var listViewDairy: ListView
    private lateinit var toggleNuts: ImageView
    private lateinit var listViewNuts: ListView
    private lateinit var toggleSeafood: ImageView
    private lateinit var listViewSeafood: ListView
    private lateinit var toggleMeat: ImageView
    private lateinit var listViewMeat: ListView
    private lateinit var toggleFruitVeg: ImageView
    private lateinit var listViewFruitVeg: ListView
    private lateinit var editTextEtc: EditText
    private lateinit var buttonRegister: Button
    private lateinit var chipGroupAllergy: ChipGroup
    private lateinit var buttonSave: Button

    private val dairyList = listOf("우유", "밀")
    private val nutsList = listOf("메밀", "땅콩", "대두", "호두")
    private val seafoodList = listOf("오징어", "조개류 (굴, 전복, 홍합 등)", "고등어", "게", "새우")
    private val meatList = listOf("돼지고기", "닭고기", "쇠고기")
    private val fruitVegList = listOf("토마토", "아황산류", "복숭아")

    private val selectedAllergies = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allergy)

        // 뷰 초기화
        backArrow = findViewById(R.id.backArrow)
        toggleDairy = findViewById(R.id.toggleDairy)
        listViewDairy = findViewById(R.id.listViewDairy)
        toggleNuts = findViewById(R.id.toggleNuts)
        listViewNuts = findViewById(R.id.listViewNuts)
        toggleSeafood = findViewById(R.id.toggleSeafood)
        listViewSeafood = findViewById(R.id.listViewSeafood)
        toggleMeat = findViewById(R.id.toggleMeat)
        listViewMeat = findViewById(R.id.listViewMeat)
        toggleFruitVeg = findViewById(R.id.toggleFruitVeg)
        listViewFruitVeg = findViewById(R.id.listViewFruitVeg)
        editTextEtc = findViewById(R.id.editTextEtc)
        buttonRegister = findViewById(R.id.buttonRegister)
        chipGroupAllergy = findViewById(R.id.chipGroupAllergy)
        buttonSave = findViewById(R.id.buttonSave)

        // 각 카테고리별 리스트뷰 설정
        setupListView(toggleDairy, listViewDairy, dairyList)
        setupListView(toggleNuts, listViewNuts, nutsList)
        setupListView(toggleSeafood, listViewSeafood, seafoodList)
        setupListView(toggleMeat, listViewMeat, meatList)
        setupListView(toggleFruitVeg, listViewFruitVeg, fruitVegList)

        // 기타 항목 등록 버튼
        buttonRegister.setOnClickListener {
            val etcText = editTextEtc.text.toString().trim()
            if (etcText.isNotEmpty()) {
                if (selectedAllergies.contains(etcText)) {
                    // 이미 등록된 항목
                    Toast.makeText(this, "이미 등록되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 새로 등록
                    selectedAllergies.add(etcText)
                    addChip(etcText)
                    editTextEtc.text.clear()
                }
            }
        }

        // 저장 버튼 (단순 "저장되었습니다." 메시지)
        buttonSave.setOnClickListener {
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 뒤로가기 버튼
        backArrow.setOnClickListener {
            finish() // 현재 액티비티 종료
        }
    }

    // 리스트뷰와 토글 설정
    private fun setupListView(toggle: ImageView, listView: ListView, items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        // 토글 열고 닫기
        toggle.setOnClickListener {
            if (listView.visibility == View.GONE) {
                listView.visibility = View.VISIBLE
                toggle.setImageResource(R.drawable.toggle_open)
                // 리스트가 실제로 VISIBLE 상태가 된 뒤 높이 재계산
                listView.post {
                    setListViewHeightBasedOnChildren(listView)
                }
            } else {
                listView.visibility = View.GONE
                toggle.setImageResource(R.drawable.toggle_closed)
            }
        }

        // 리스트뷰 항목 클릭 이벤트
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            if (selectedAllergies.contains(selectedItem)) {
                // 이미 등록된 항목 -> 중복 알림
                Toast.makeText(this, "이미 등록되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 새로 등록
                selectedAllergies.add(selectedItem)
                addChip(selectedItem)
            }
        }
    }

    // Chip 추가 메서드
    private fun addChip(text: String) {
        val chip = Chip(this).apply {
            this.text = text
            isCloseIconVisible = true

            // 흰색 배경 + 회색 테두리
            chipBackgroundColor = getColorStateList(R.color.white)
            chipStrokeColor = getColorStateList(R.color.light_gray)
            chipStrokeWidth = 2f

            // Chip의 X 아이콘 클릭 -> 제거
            setOnCloseIconClickListener {
                chipGroupAllergy.removeView(this)
                selectedAllergies.remove(text)
            }
        }
        chipGroupAllergy.addView(chip)
    }

    // Chip 제거 메서드
    private fun removeChip(text: String) {
        for (i in 0 until chipGroupAllergy.childCount) {
            val chip = chipGroupAllergy.getChildAt(i) as Chip
            if (chip.text == text) {
                chipGroupAllergy.removeView(chip)
                break
            }
        }
    }

    // ScrollView 안 ListView 높이 동적 설정
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
        // dividerHeight * (count - 1) -> 구분선 높이 고려
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }
}
