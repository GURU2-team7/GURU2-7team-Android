package com.example.guru2

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.databinding.ActivityAllergyBinding
import com.google.android.material.chip.Chip

class AllergyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllergyBinding

    private val dairyList = listOf("우유", "밀")
    private val nutsList = listOf("메밀", "땅콩", "대두", "호두")
    private val seafoodList = listOf("오징어", "조개류 (굴, 전복, 홍합 등)", "고등어", "게", "새우")
    private val meatList = listOf("돼지고기", "닭고기", "쇠고기")
    private val fruitVegList = listOf("토마토", "아황산류", "복숭아")

    private val selectedAllergies = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivityAllergyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 각 카테고리별 리스트뷰 설정
        setupListView(binding.toggleDairy, binding.listViewDairy, dairyList)
        setupListView(binding.toggleNuts, binding.listViewNuts, nutsList)
        setupListView(binding.toggleSeafood, binding.listViewSeafood, seafoodList)
        setupListView(binding.toggleMeat, binding.listViewMeat, meatList)
        setupListView(binding.toggleFruitVeg, binding.listViewFruitVeg, fruitVegList)

        // 기타 항목 등록 버튼
        binding.buttonRegister.setOnClickListener {
            val etcText = binding.editTextEtc.text.toString().trim()
            if (etcText.isNotEmpty()) {
                if (selectedAllergies.contains(etcText)) {
                    // 이미 등록된 항목
                    Toast.makeText(this, "이미 등록되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 새로 등록
                    selectedAllergies.add(etcText)
                    addChip(etcText)
                    binding.editTextEtc.text.clear()
                }
            }
        }

        // 저장 버튼
        binding.buttonSave.setOnClickListener {
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 뒤로가기 버튼
        binding.backArrow.setOnClickListener {
            finish() // 현재 액티비티 종료
        }
    }

    // 리스트뷰와 토글 설정
    private fun setupListView(toggle: View, listView: View, items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        (listView as? android.widget.ListView)?.adapter = adapter

        // 토글 열고 닫기
        toggle.setOnClickListener {
            if (listView.visibility == View.GONE) {
                listView.visibility = View.VISIBLE
                (toggle as? android.widget.ImageView)?.setImageResource(R.drawable.toggle_open)
                // 리스트가 실제로 VISIBLE 상태가 된 뒤 높이 재계산
                listView.post {
                    setListViewHeightBasedOnChildren(listView)
                }
            } else {
                listView.visibility = View.GONE
                (toggle as? android.widget.ImageView)?.setImageResource(R.drawable.toggle_closed)
            }
        }

        // 리스트뷰 항목 클릭 이벤트
        (listView as? android.widget.ListView)?.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            if (selectedAllergies.contains(selectedItem)) {
                Toast.makeText(this, "이미 등록되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
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
                binding.chipGroupAllergy.removeView(this)
                selectedAllergies.remove(text)
            }
        }
        binding.chipGroupAllergy.addView(chip)
    }

    // ScrollView 안 ListView 높이 동적 설정
    private fun setListViewHeightBasedOnChildren(listView: View) {
        val listAdapter = (listView as? android.widget.ListView)?.adapter ?: return
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
        params.height = totalHeight + ((listView as? android.widget.ListView)?.dividerHeight ?: 0 * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }
}
