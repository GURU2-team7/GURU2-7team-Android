package com.example.guru2.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val items = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    fun updateFragments(items: List<Fragment>) {
        this.items.clear()  // 기존 데이터를 지움
        this.items.addAll(items)  // 새 데이터를 추가
        notifyDataSetChanged()  // Adapter를 갱신
    }
}