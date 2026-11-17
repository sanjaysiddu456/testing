package com.example.testapplication


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.R
import com.example.testapplication.adapters.StockEnquiryAdapter
import com.example.testapplication.databinding.ActivityStockDetailsBinding
import com.example.testapplication.utils.ApiStatus

class StockDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStockDetailsBinding

    private lateinit var enquiryAdapter: StockEnquiryAdapter

    private val viewModel: StockViewModel by viewModels {
        StockViewModelFactory(applicationContext)
    }

    companion object {
        const val EXTRA_STOCK_ID = "extra_stock_id"
    }

    private var stockId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStockDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stockId = intent.getIntExtra(EXTRA_STOCK_ID, -1)

        if (stockId == -1) {
            Toast.makeText(
                this,
                "Stock details not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setupClickListeners()

        setupEnquiryRecyclerView()

        observeStockDetails()

        viewModel.getStocks()
    }

    // ==========================================
    // SETUP CLICK LISTENERS
    // ==========================================

    private fun setupClickListeners() {

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    // ==========================================
    // ENQUIRY RECYCLER VIEW
    // ==========================================

    private fun setupEnquiryRecyclerView() {

        enquiryAdapter = StockEnquiryAdapter()

        binding.rvStockEnquiries.apply {

            layoutManager = LinearLayoutManager(
                this@StockDetailsActivity
            )

            adapter = enquiryAdapter

            isNestedScrollingEnabled = false
        }
    }

    // ==========================================
    // OBSERVE STOCK DETAILS
    // ==========================================

    private fun observeStockDetails() {

        viewModel.stockResponse.observe(this) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    // Show loading if you add a progress bar.
                }

                ApiStatus.Status.SUCCESS -> {

                    val selectedStock = resource.data?.data
                        ?.find { it.id == stockId }

                    if (selectedStock != null) {

                        displayStockDetails(selectedStock)

                    } else {

                        Toast.makeText(
                            this,
                            "Stock not found",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    }
                }

                ApiStatus.Status.ERROR -> {

                    Toast.makeText(
                        this,
                        resource.message ?: "Unable to fetch stock details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openStockImageViewer(images: List<String>) {

        if (images.isEmpty()) {
            return
        }

        val intent = Intent(
            this,
            StockImageViewerActivity::class.java
        )

        intent.putStringArrayListExtra(
            StockImageViewerActivity.EXTRA_IMAGES,
            ArrayList(images)
        )

        startActivity(intent)
    }

    // ==========================================
    // DISPLAY STOCK DETAILS
    // ==========================================

    private fun displayStockDetails(
        stock: com.example.testapplication.datamodels.Stock
    ) {

        binding.tvDetailsCarName.text = stock.carName

        binding.tvDetailsStockNumber.text = stock.stockNumber

        binding.tvDetailsAvailability.text = stock.availability

        binding.tvDetailsAvailability.setTextColor(
            if (stock.availability.equals("In Stock", true)) {
                android.graphics.Color.rgb(0, 159, 227)
            } else {
                android.graphics.Color.BLACK
            }
        )

        binding.tvStockDate.text =
            "In stock from ${stock.stockDate}"

        // Display the first available image.

        val firstImage = stock.images.firstOrNull()

        val imageResource = firstImage?.let {
            resources.getIdentifier(
                it,
                "drawable",
                packageName
            )
        } ?: 0

        if (imageResource != 0) {
            binding.ivStockDetailsImage.setImageResource(imageResource)
        } else {
            binding.ivStockDetailsImage.setImageResource(
                R.drawable.ic_launcher_background
            )
        }

        // Open full-screen image viewer.

        binding.ivStockDetailsImage.setOnClickListener {

            openStockImageViewer(stock.images)
        }

        // Display enquiries.

        if (stock.enquiries.isNotEmpty()) {

            binding.rvStockEnquiries.visibility = View.VISIBLE
            binding.layoutNoEnquiries.visibility = View.GONE

            enquiryAdapter.submitList(stock.enquiries)

        } else {

            binding.rvStockEnquiries.visibility = View.GONE
            binding.layoutNoEnquiries.visibility = View.VISIBLE

            enquiryAdapter.submitList(emptyList())
        }
    }
}