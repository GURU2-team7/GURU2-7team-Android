package com.example.guru2.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.guru2.R
import com.example.guru2.allergy.AllergyActivity
import com.example.guru2.databinding.FragmentHomeBinding
import com.example.guru2.fridge.FridgeFragment
import com.example.guru2.recipe.BookmarkFragment
import com.example.guru2.recipe.Recipe1
import com.example.guru2.recipe.RecipeFragment
import java.util.Timer
import kotlin.concurrent.timer

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var autoSlideTimer: Timer? = null

    private val REQUEST_READ_EXTERNAL_STORAGE = 1000
    lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewPager = binding.viewPager

        // 이미지 권한 요청 및 사진 로드
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                val dlg = AlertDialog.Builder(requireContext())
                dlg.setTitle("권한이 필요한 이유")
                dlg.setMessage("사진 정보를 얻기 위해 저장소 접근 권한이 필요합니다.")
                dlg.setPositiveButton("확인") { _, _ ->
                    requestPermissions(arrayOf(permission), REQUEST_READ_EXTERNAL_STORAGE)
                }
                dlg.setNegativeButton("취소", null)
                dlg.show()
            } else {
                requestPermissions(arrayOf(permission), REQUEST_READ_EXTERNAL_STORAGE)
            }
        } else {
            getAllPhotos()
        }

        // 버튼 클릭 이벤트 설정
        // AllergyActivity로 이동
        binding.buttonGoToAllergy.setOnClickListener {
            val intent = Intent(requireContext(), AllergyActivity::class.java)
            startActivity(intent)
        }

        // FridgeFragment로 이동
        binding.buttonGoToFridge.setOnClickListener {
            navigateToFragment(FridgeFragment())
        }

        // RecipeFragment로 이동
        binding.buttonGoToAskRecipe.setOnClickListener {
            navigateToFragment(RecipeFragment())
        }

        // BookmarkFragment로 이동
        binding.buttonSavedRecipes.setOnClickListener {
            navigateToFragment(BookmarkFragment())
        }

        binding.buttonGoToAskRecipe.setOnClickListener {
            navigateToFragment(Recipe1())

        }

        return binding.root
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.rootlayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAllPhotos()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("권한 거부됨")
                    .setMessage("권한이 거부되어 사진을 불러올 수 없습니다.")
                    .setPositiveButton("확인", null)
                    .show()
            }
        }
    }

    private fun getAllPhotos() {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        val fragments = ArrayList<Fragment>()
        cursor?.use {
            val columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(columnIndexId)
                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(id.toString()).build()
                fragments.add(ImageFragment.newInstance(contentUri.toString()))
            }
        }

        val adapter = MyPagerAdapter(requireActivity().supportFragmentManager)
        adapter.updateFragments(fragments)
        viewPager.adapter = adapter

        autoSlideTimer?.cancel()
        autoSlideTimer = timer(period = 3000) {
            requireActivity().runOnUiThread {
                if (viewPager.currentItem < adapter.count - 1) {
                    viewPager.currentItem = viewPager.currentItem + 1
                } else {
                    viewPager.currentItem = 0
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoSlideTimer?.cancel()
        autoSlideTimer = null
        _binding = null
    }

    interface OnFragmentChangeListener {
        fun onChangeFragment(fragment: Fragment)
    }
}
