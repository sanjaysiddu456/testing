package com.example.testapplication.enquiryFeature

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.viewmodels.FollowUpEnquiryViewModel
import com.example.testapplication.adapter.FollowUpAdapter
import com.example.testapplication.datamodels.Enquiry
import com.example.testapplication.datamodels.FollowUp
import com.example.testapplication.databinding.ActivitySellingEnquiryDetailsBinding
import com.example.testapplication.utils.ApiStatus

class SellingEnquiryDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellingEnquiryDetailsBinding

    private lateinit var followUpAdapter: FollowUpAdapter

    private var enquiry: Enquiry? = null

    private val token: String
        get() = intent.getStringExtra(EXTRA_TOKEN).orEmpty()

    private val viewModel: FollowUpEnquiryViewModel by viewModels {
        EnquiryViewModelFactory(applicationContext)
    }

    companion object {

        const val EXTRA_ENQUIRY = "extra_enquiry"
        const val EXTRA_TOKEN = "extra_token"
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySellingEnquiryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enquiry = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_ENQUIRY, Enquiry::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_ENQUIRY) as? Enquiry
        }

        if (enquiry == null) {
            Toast.makeText(
                this,
                "Enquiry details not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        displayEnquiryDetails()

        setupFollowUpRecyclerView()

        setupClickListeners()

        observeFollowUps()

        loadFollowUps()
    }

    // ==========================================
    // DISPLAY CUSTOMER AND ENQUIRY DETAILS
    // ==========================================

    private fun displayEnquiryDetails() {

        val item = enquiry ?: return

        // Customer details

        binding.tvCustomerName.text = item.customerName

        binding.tvCertified.text =
            if (item.isCertified) "Certified" else "Not Certified"

        binding.tvCertified.setTextColor(
            if (item.isCertified) {
                Color.rgb(117, 185, 0)
            } else {
                Color.GRAY
            }
        )

        binding.tvMobileNumber.text = "+91 ${item.mobileNumber}"

        binding.tvEmail.text = item.email

        // Callback information

        binding.tvCallBackIn.text = item.callBackIn

        binding.tvCallBackDateTime.text =
            "${item.callBackDate}, ${item.callBackTime}"

        // Enquiry information

        binding.rowEnquiryNo.tvDetailLabel.text = "Enquiry No"
        binding.rowEnquiryNo.tvDetailValue.text = item.enquiryNo

        binding.rowDateOfEnquiry.tvDetailLabel.text = "Date of Enquiry"
        binding.rowDateOfEnquiry.tvDetailValue.text = item.dateOfEnquiry

        binding.rowTimeOfEnquiry.tvDetailLabel.text = "Time of Enquiry"
        binding.rowTimeOfEnquiry.tvDetailValue.text = item.timeOfEnquiry

        binding.rowEnquirySource.tvDetailLabel.text = "Enquiry Source"
        binding.rowEnquirySource.tvDetailValue.text = item.enquirySource

        // Vehicle information

        binding.rowRegName.tvDetailLabel.text = "Reg. Name"
        binding.rowRegName.tvDetailValue.text = item.regName

        binding.rowRegNo.tvDetailLabel.text = "Reg. No"
        binding.rowRegNo.tvDetailValue.text = item.regNo

        binding.rowMaker.tvDetailLabel.text = "Maker"
        binding.rowMaker.tvDetailValue.text = item.maker

        binding.rowModel.tvDetailLabel.text = "Model"
        binding.rowModel.tvDetailValue.text = item.model

        binding.rowCar.tvDetailLabel.text = "Car"
        binding.rowCar.tvDetailValue.text = item.carName

        // Open or closed enquiry UI

        updateEnquiryStatusUI()
    }

    // ==========================================
    // OPEN / CLOSED ENQUIRY VISIBILITY
    // ==========================================

    private fun updateEnquiryStatusUI() {

        val item = enquiry ?: return

        if (item.isClosed) {

            // CLOSED ENQUIRY

            binding.tvEnquiryClosedBanner.visibility = View.VISIBLE

            binding.tvEnquiryClosedBanner.text =
                "Enquiry closed on ${item.closedDateTime}"

            // Hide Call Customer and Close Enquiry

            binding.layoutEnquiryActions.visibility = View.GONE

            // Hide New Follow-up

            binding.tvNewFollowUp.visibility = View.GONE

            // Hide Create Follow-up button

            binding.btnCreateFollowUp.visibility = View.GONE

        } else {

            // OPEN ENQUIRY

            binding.tvEnquiryClosedBanner.visibility = View.GONE

            // Show Call Customer and Close Enquiry

            binding.layoutEnquiryActions.visibility = View.VISIBLE

            // Follow-up button visibility is handled
            // after receiving the follow-up response.
        }
    }

    // ==========================================
    // FOLLOW-UP RECYCLER VIEW
    // ==========================================

    private fun setupFollowUpRecyclerView() {

        followUpAdapter = FollowUpAdapter()

        binding.rvFollowUps.apply {

            layoutManager = LinearLayoutManager(
                this@SellingEnquiryDetailsActivity
            )

            adapter = followUpAdapter

            isNestedScrollingEnabled = false
        }
    }

    // ==========================================
    // GET FOLLOW-UP DATA
    // ==========================================

    private fun loadFollowUps() {

        val selectedEnquiry = enquiry ?: return

        viewModel.getFollowUps(
            enquiryId = selectedEnquiry.id,
            token = token
        )
    }

    // ==========================================
    // OBSERVE FOLLOW-UP RESPONSE
    // ==========================================

    private fun observeFollowUps() {

        viewModel.followUpResponse.observe(this) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    hideAllFollowUpViews()

                    binding.progressFollowUps.visibility = View.VISIBLE
                }

                ApiStatus.Status.SUCCESS -> {

                    binding.progressFollowUps.visibility = View.GONE

                    val followUps = resource.data?.data.orEmpty()

                    if (followUps.isNotEmpty()) {

                        // Follow-ups available

                        binding.layoutNoFollowUps.visibility = View.GONE

                        binding.layoutFollowUpError.visibility = View.GONE

                        binding.rvFollowUps.visibility = View.VISIBLE

                        // New Follow-up only for OPEN enquiries

                        binding.tvNewFollowUp.visibility =
                            if (enquiry?.isClosed == false) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }

                        followUpAdapter.submitList(followUps)

                    } else {

                        // No follow-ups available

                        binding.rvFollowUps.visibility = View.GONE

                        binding.layoutFollowUpError.visibility = View.GONE

                        binding.layoutNoFollowUps.visibility = View.VISIBLE

                        // Create button only for OPEN enquiries

                        binding.btnCreateFollowUp.visibility =
                            if (enquiry?.isClosed == false) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }

                        binding.tvNewFollowUp.visibility = View.GONE

                        followUpAdapter.submitList(emptyList<FollowUp>())
                    }
                }

                ApiStatus.Status.ERROR -> {

                    hideAllFollowUpViews()

                    binding.layoutFollowUpError.visibility = View.VISIBLE

                    binding.tvFollowUpError.text =
                        resource.message ?: "Unable to load follow-ups"
                }
            }
        }
    }

    private fun hideAllFollowUpViews() {

        binding.progressFollowUps.visibility = View.GONE

        binding.layoutNoFollowUps.visibility = View.GONE

        binding.rvFollowUps.visibility = View.GONE

        binding.layoutFollowUpError.visibility = View.GONE

        binding.tvNewFollowUp.visibility = View.GONE
    }

    // ==========================================
    // CLICK LISTENERS
    // ==========================================

    private fun setupClickListeners() {

        // Call Customer

        binding.btnCallCustomer.setOnClickListener {

            val mobileNumber = enquiry?.mobileNumber.orEmpty()

            if (mobileNumber.isNotBlank()) {

                val dialIntent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:$mobileNumber")
                )

                startActivity(dialIntent)

            } else {

                Toast.makeText(
                    this,
                    "Mobile number not available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Close Enquiry

        binding.btnCloseEnquiry.setOnClickListener {

            showCloseEnquiryDialog()
        }

        // Create Follow-up from empty state

        binding.btnCreateFollowUp.setOnClickListener {

            openCreateFollowUp()
        }

        // New Follow-up when records exist

        binding.tvNewFollowUp.setOnClickListener {

            openCreateFollowUp()
        }

        // Retry follow-up request

        binding.btnRetryFollowUps.setOnClickListener {

            loadFollowUps()
        }
    }

    // ==========================================
    // CLOSE ENQUIRY
    // ==========================================

    private fun showCloseEnquiryDialog() {

//        AlertDialog.Builder(this)
//            .setTitle("Close Enquiry")
//            .setMessage("Are you sure you want to close this enquiry?")
//            .setPositiveButton("Yes") { dialog, _ ->
//
//                // TODO:
//                // Call your Close Enquiry API here.
//                // Update isClosed only after successful API response.
//
//                Toast.makeText(
//                    this,
//                    "Close Enquiry API is not connected yet",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
    }

    // ==========================================
    // CREATE FOLLOW-UP
    // ==========================================

    private fun openCreateFollowUp() {

        val selectedEnquiry = enquiry ?: return

        if (selectedEnquiry.isClosed) {
            return
        }

        // TODO:
        // Open your Create Follow-up Activity or BottomSheet.
        // Pass selectedEnquiry.id to it.

        Toast.makeText(
            this,
            "Create Follow-up for enquiry ${selectedEnquiry.id}",
            Toast.LENGTH_SHORT
        ).show()
    }
}