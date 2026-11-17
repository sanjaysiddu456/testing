package com.example.testapplication


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.adapter.FollowUpAdapter
import com.example.testapplication.datamodels.Enquiry
import com.example.testapplication.databinding.ActivitySellingEnquiryDetailsBinding
import com.example.testapplication.utils.ApiStatus


class SellingEnquiryDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellingEnquiryDetailsBinding

    private lateinit var followUpAdapter: FollowUpAdapter

    private var enquiry: Enquiry? = null

    // Replace with your actual saved login token.
    private val token: String
        get() = ""

    private val viewModel: FollowUpEnquiryViewModel by viewModels {
        FollowUpEnquiryViewModelFactory(applicationContext)
    }

    companion object {
        const val EXTRA_ENQUIRY = "extra_enquiry"
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySellingEnquiryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enquiry = intent.getSerializableExtra(EXTRA_ENQUIRY) as? Enquiry

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

    // ------------------------------------------------
    // CUSTOMER DETAILS
    // ------------------------------------------------

    private fun displayEnquiryDetails() {

        val item = enquiry ?: return

        binding.tvCustomerName.text = item.customerName

        binding.tvCertified.text =
            if (item.isCertified) "Certified" else "Not Certified"

        binding.tvMobileNumber.text = "+91 ${item.mobileNumber}"

        binding.tvEmail.text = item.email

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

        binding.tvCallBackIn.text = item.callBackIn

        binding.tvCallBackDateTime.text =
            "${item.callBackDate}, ${item.callBackTime}"
    }

    // ------------------------------------------------
    // FOLLOW-UP RECYCLERVIEW
    // ------------------------------------------------

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

    // ------------------------------------------------
    // LOAD FOLLOW-UPS
    // ------------------------------------------------

    private fun loadFollowUps() {

        val selectedEnquiry = enquiry ?: return

        viewModel.getFollowUps(
            enquiryId = selectedEnquiry.id,
            token = token
        )
    }

    // ------------------------------------------------
    // OBSERVE FOLLOW-UP RESPONSE
    // ------------------------------------------------

    private fun observeFollowUps() {

        viewModel.followUpResponse.observe(this) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    hideFollowUpStates()

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

                        binding.tvNewFollowUp.visibility = View.VISIBLE

                        followUpAdapter.submitList(followUps)

                    } else {

                        // No follow-ups available

                        binding.rvFollowUps.visibility = View.GONE
                        binding.layoutFollowUpError.visibility = View.GONE

                        binding.layoutNoFollowUps.visibility = View.VISIBLE

                        binding.tvNewFollowUp.visibility = View.GONE

                        followUpAdapter.submitList(emptyList())
                    }
                }

                ApiStatus.Status.ERROR -> {

                    hideFollowUpStates()

                    binding.layoutFollowUpError.visibility = View.VISIBLE

                    binding.tvFollowUpError.text =
                        resource.message ?: "Unable to load follow-ups"
                }
            }
        }
    }

    private fun hideFollowUpStates() {

        binding.progressFollowUps.visibility = View.GONE
        binding.layoutNoFollowUps.visibility = View.GONE
        binding.rvFollowUps.visibility = View.GONE
        binding.layoutFollowUpError.visibility = View.GONE
        binding.tvNewFollowUp.visibility = View.GONE
    }

    // ------------------------------------------------
    // CLICK LISTENERS
    // ------------------------------------------------

    private fun setupClickListeners() {

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

        binding.btnCloseEnquiry.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Close Enquiry")
                .setMessage("Are you sure you want to close this enquiry?")
                .setPositiveButton("Yes") { dialog, _ ->

                    // Connect close enquiry API here.

                    Toast.makeText(
                        this,
                        "Close enquiry API is not connected yet",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnCreateFollowUp.setOnClickListener {
            openCreateFollowUp()
        }

        binding.tvNewFollowUp.setOnClickListener {
            openCreateFollowUp()
        }

        binding.btnRetryFollowUps.setOnClickListener {
            loadFollowUps()
        }
    }

    // ------------------------------------------------
    // CREATE FOLLOW-UP
    // ------------------------------------------------

    private fun openCreateFollowUp() {

        val selectedEnquiry = enquiry ?: return

        // Replace this with your actual CreateFollowUpActivity.
        // Pass the selected enquiry ID.

        /*
        val intent = Intent(
            this,
            CreateFollowUpActivity::class.java
        )

        intent.putExtra("enquiryId", selectedEnquiry.id)

        startActivityForResult(intent, 100)
        */

        Toast.makeText(
            this,
            "Create follow-up for enquiry ${selectedEnquiry.id}",
            Toast.LENGTH_SHORT
        ).show()
    }
}