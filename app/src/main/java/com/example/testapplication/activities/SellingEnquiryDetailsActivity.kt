
package com.example.testapplication.enquiryFeature

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.activities.CreateSellingEnquiryActivity
import com.example.testapplication.activities.NewFollowUpActivity
import com.example.testapplication.adapter.FollowUpAdapter
import com.example.testapplication.datamodels.Enquiry
import com.example.testapplication.datamodels.FollowUp
import com.example.testapplication.databinding.ActivitySellingEnquiryDetailsBinding
import com.example.testapplication.utils.ApiStatus
import com.example.testapplication.viewmodels.FollowUpEnquiryViewModel

class SellingEnquiryDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellingEnquiryDetailsBinding

    private lateinit var followUpAdapter: FollowUpAdapter

    private var enquiry: Enquiry? = null

    private var enquiryId: Int = -1

    private var isSalesConsultant = false
    private var isManager = false

    private val token: String
        get() = intent.getStringExtra(EXTRA_TOKEN).orEmpty()

    // Enquiry Details ViewModel
    private val enquiryViewModel: EnquiryViewModel by viewModels {
        EnquiryViewModelFactory(applicationContext)
    }

    // Follow-ups ViewModel
    private val followUpViewModel: FollowUpEnquiryViewModel by viewModels {
        EnquiryViewModelFactory(applicationContext)
    }

    companion object {

        const val EXTRA_ENQUIRY_ID = "extra_enquiry_id"
        const val EXTRA_TOKEN = "extra_token"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySellingEnquiryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enquiryId = intent.getIntExtra(EXTRA_ENQUIRY_ID, -1)

//        if (enquiryId == -1) {
//
//            Toast.makeText(
//                this,
//                "Enquiry ID not found",
//                Toast.LENGTH_SHORT
//            ).show()
//
//            finish()
//            return
//        }

        getLoggedInUserType()

        setupFollowUpRecyclerView()

        setupClickListeners()

        observeEnquiryDetails()

        observeFollowUps()

        // =====================================
        // INDEPENDENT API CALLS
        // =====================================

        // Call 1: Enquiry details
        enquiryViewModel.getEnquiryDetails(
            enquiryId = enquiryId,
            token = token
        )

        // Call 2: Follow-ups
        followUpViewModel.getFollowUps(
            enquiryId = enquiryId,
            token = token
        )
    }

    private val editEnquiryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                enquiryViewModel.getEnquiryDetails(
                    enquiryId = enquiryId,
                    ""
                )
            }
        }

    // ==========================================
    // USER ROLE
    // ==========================================

    private fun getLoggedInUserType() {

        val userType = GlobalSharedPreference.getSharedPreference(
            "login",
            this
        ).getString("user", "").orEmpty()

        isSalesConsultant =
            userType == LoginUsers.SALES_CONSULTANT

        isManager =
            userType == LoginUsers.H_PROMISE_MANAGER
    }

    // ==========================================
    // OBSERVE ENQUIRY DETAILS API
    // ==========================================

    private fun observeEnquiryDetails() {

        enquiryViewModel.enquiryDetailsResponse.observe(this) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    // Show enquiry details loader if available.
                }

                ApiStatus.Status.SUCCESS -> {

                    val details = resource.data?.data

                    if (details == null) {

                        Toast.makeText(
                            this,
                            "Enquiry details not available",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@observe
                    }

                    enquiry = details

                    displayEnquiryDetails(details)

                    updateEnquiryStatusUI()
                }

                ApiStatus.Status.ERROR -> {

                    Toast.makeText(
                        this,
                        resource.message ?: "Unable to load enquiry details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ==========================================
    // DISPLAY ENQUIRY DETAILS
    // ==========================================

    private fun displayEnquiryDetails(item: Enquiry) {

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

        binding.tvCallBackIn.text = item.callBackIn

        binding.tvCallBackDateTime.text =
            "${item.callBackDate}, ${item.callBackTime}"

        binding.rowEnquiryNo.tvDetailLabel.text = "Enquiry No"
        binding.rowEnquiryNo.tvDetailValue.text = item.enquiryNo

        binding.rowDateOfEnquiry.tvDetailLabel.text = "Date of Enquiry"
        binding.rowDateOfEnquiry.tvDetailValue.text = item.dateOfEnquiry

        binding.rowTimeOfEnquiry.tvDetailLabel.text = "Time of Enquiry"
        binding.rowTimeOfEnquiry.tvDetailValue.text = item.timeOfEnquiry

        binding.rowEnquirySource.tvDetailLabel.text = "Enquiry Source"
        binding.rowEnquirySource.tvDetailValue.text = item.enquirySource

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
    }

    // ==========================================
    // OPEN / CLOSED ENQUIRY UI
    // ==========================================

    private fun updateEnquiryStatusUI() {

        val item = enquiry ?: return

        if (item.isClosed) {

            binding.tvEnquiryClosedBanner.visibility = View.VISIBLE

            binding.tvEnquiryClosedBanner.text =
                "Enquiry closed on ${item.closedDateTime}"

            binding.layoutEnquiryActions.visibility = View.GONE

        } else {

            binding.tvEnquiryClosedBanner.visibility = View.GONE

            binding.layoutEnquiryActions.visibility = View.VISIBLE
        }

        // Reapply role and enquiry status rules
        // after enquiry details are received.
        updateFollowUpActionVisibility(
            hasFollowUps = followUpAdapter.itemCount > 0
        )
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
    // OBSERVE FOLLOW-UPS API
    // ==========================================

    private fun observeFollowUps() {

        followUpViewModel.followUpResponse.observe(this) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    hideFollowUpContent()

                    binding.progressFollowUps.visibility = View.VISIBLE
                }

                ApiStatus.Status.SUCCESS -> {

                    binding.progressFollowUps.visibility = View.GONE

                    val followUps = resource.data?.data.orEmpty()

                    if (followUps.isNotEmpty()) {

                        binding.layoutNoFollowUps.visibility = View.GONE
                        binding.layoutFollowUpError.visibility = View.GONE
                        binding.rvFollowUps.visibility = View.VISIBLE

                        followUpAdapter.submitList(followUps)

                        updateFollowUpActionVisibility(
                            hasFollowUps = true
                        )

                    } else {

                        binding.rvFollowUps.visibility = View.GONE
                        binding.layoutFollowUpError.visibility = View.GONE
                        binding.layoutNoFollowUps.visibility = View.VISIBLE

                        followUpAdapter.submitList(emptyList<FollowUp>())

                        updateFollowUpActionVisibility(
                            hasFollowUps = false
                        )
                    }
                }

                ApiStatus.Status.ERROR -> {

                    hideFollowUpContent()

                    binding.layoutFollowUpError.visibility = View.VISIBLE

                    binding.tvFollowUpError.text =
                        resource.message ?: "Unable to load follow-ups"
                }
            }
        }
    }

    // ==========================================
    // ROLE-BASED FOLLOW-UP ACTIONS
    // ==========================================

    private fun updateFollowUpActionVisibility(
        hasFollowUps: Boolean
    ) {

        val isOpen = enquiry?.isClosed == false

        // Manager and other roles cannot create follow-ups.
        if (!isSalesConsultant || isManager || !isOpen) {

            binding.tvNewFollowUp.visibility = View.GONE
            binding.btnCreateFollowUp.visibility = View.GONE

            return
        }

        // Sales Consultant only
        if (hasFollowUps) {

            binding.tvNewFollowUp.visibility = View.VISIBLE
            binding.btnCreateFollowUp.visibility = View.GONE

        } else {

            binding.tvNewFollowUp.visibility = View.GONE
            binding.btnCreateFollowUp.visibility = View.VISIBLE
        }
    }

    // ==========================================
    // HIDE FOLLOW-UP CONTENT
    // ==========================================

    private fun hideFollowUpContent() {

        binding.progressFollowUps.visibility = View.GONE
        binding.layoutNoFollowUps.visibility = View.GONE
        binding.rvFollowUps.visibility = View.GONE
        binding.layoutFollowUpError.visibility = View.GONE

        binding.tvNewFollowUp.visibility = View.GONE
        binding.btnCreateFollowUp.visibility = View.GONE
    }

    // ==========================================
    // CLICK LISTENERS
    // ==========================================

    private fun setupClickListeners() {




        binding.btnEdit.setOnClickListener {

            val intent = Intent(
                this,
                CreateSellingEnquiryActivity::class.java
            ).apply {
                putExtra(
                    CreateSellingEnquiryActivity.EXTRA_MODE,
                    CreateSellingEnquiryActivity.MODE_EDIT
                )

                putExtra(
                    CreateSellingEnquiryActivity.EXTRA_ENQUIRY_ID,
                    enquiryId
                )

                putExtra(
                    CreateSellingEnquiryActivity.EXTRA_TOKEN,
                    authToken
                )
            }

            editEnquiryLauncher.launch(intent)
        }

        binding.btnCallCustomer.setOnClickListener {

            val mobileNumber = enquiry?.mobileNumber.orEmpty()

            if (mobileNumber.isNotBlank()) {

                startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:$mobileNumber")
                    )
                )

            } else {

                Toast.makeText(
                    this,
                    "Mobile number not available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        binding.btnCloseEnquiry.setOnClickListener {

            showCloseEnquiryDialog()
        }

        binding.btnCreateFollowUp.setOnClickListener {

            if (isSalesConsultant && !isManager) {
                openCreateFollowUp()
            }
        }

        binding.tvNewFollowUp.setOnClickListener {

            if (isSalesConsultant && !isManager) {
                openCreateFollowUp()
            }
        }

        binding.btnRetryFollowUps.setOnClickListener {

            followUpViewModel.getFollowUps(
                enquiryId = enquiryId,
                token = token
            )
        }
    }

    // ==========================================
    // CLOSE ENQUIRY
    // ==========================================

    private fun showCloseEnquiryDialog() {

        // Keep your existing Close Enquiry dialog
        // and API implementation here.
    }

    // ==========================================
    // CREATE FOLLOW-UP
    // ==========================================

    private fun openCreateFollowUp() {

        if (!isSalesConsultant || isManager) return

        val selectedEnquiry = enquiry ?: return

        if (selectedEnquiry.isClosed) return



         val intent = Intent(
             this,
             NewFollowUpActivity::class.java
         )

         intent.putExtra("ENQUIRY_ID", selectedEnquiry.id)
         startActivity(intent)
    }
}
