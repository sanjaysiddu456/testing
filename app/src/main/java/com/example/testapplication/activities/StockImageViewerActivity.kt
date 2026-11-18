package com.example.testapplication.activities

import com.example.testapplication.adapters.StockImageAdapter



import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityStockImageViewerBinding

class StockImageViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStockImageViewerBinding

    private lateinit var imageAdapter: StockImageAdapter

    private var images = arrayListOf<String>()

    companion object {

        const val EXTRA_IMAGES = "extra_stock_images"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStockImageViewerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Receive images from Stock Details Activity

        images = intent.getStringArrayListExtra(EXTRA_IMAGES)
            ?: arrayListOf()

        if (images.isEmpty()) {
            finish()
            return
        }

        setupImageSlider()

        setupDots()

        setupClickListeners()
    }

    // ==========================================
    // IMAGE SLIDER
    // ==========================================

    private fun setupImageSlider() {

        imageAdapter = StockImageAdapter(images)

        binding.viewPagerStockImages.adapter = imageAdapter

        binding.viewPagerStockImages.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {

                    super.onPageSelected(position)

                    updateDots(position)
                }
            }
        )
    }

    // ==========================================
    // CREATE DOTS
    // ==========================================

    private fun setupDots() {

        binding.layoutImageDots.removeAllViews()

        images.forEach {

            val dot = TextView(this).apply {

                text = "●"

                textSize = 9f

                gravity = Gravity.CENTER

                setPadding(5.dp, 0, 5.dp, 0)

                setTextColor(
                    getColor(R.color.stock_dot_unselected)
                )
            }

            binding.layoutImageDots.addView(dot)
        }

        updateDots(0)
    }

    // ==========================================
    // UPDATE SELECTED DOT
    // ==========================================

    private fun updateDots(selectedPosition: Int) {

        for (index in 0 until binding.layoutImageDots.childCount) {

            val dot = binding.layoutImageDots.getChildAt(index) as TextView

            if (index == selectedPosition) {

                dot.setTextColor(
                    getColor(R.color.stock_dot_selected)
                )

            } else {

                dot.setTextColor(
                    getColor(R.color.stock_dot_unselected)
                )
            }
        }
    }

    // ==========================================
    // CLICK LISTENERS
    // ==========================================

    private fun setupClickListeners() {

        binding.btnCloseImageViewer.setOnClickListener {

            finish()
        }
    }

    // ==========================================
    // DP CONVERSION
    // ==========================================

    private val Int.dp: Int
        get() = (
                this * resources.displayMetrics.density
                ).toInt()
}