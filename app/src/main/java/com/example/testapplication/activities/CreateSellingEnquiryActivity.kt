package com.example.testapplication.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.testapplication.R
import com.example.testapplication.datamodels.CreateSellingEnquiryRequest
import com.example.testapplication.fragments.CustomerDetails
import com.example.testapplication.fragments.CustomerDetailsFragment
import com.example.testapplication.fragments.EnquiryDetails
import com.example.testapplication.fragments.EnquiryDetailsFragment
import com.example.testapplication.utils.ApiStatus
import com.example.testapplication.viewmodels.CreateSellingEnquiryViewModel
import com.example.testapplication.viewmodels.CreateSellingEnquiryViewModelFactory

class CreateSellingEnquiryActivity : AppCompatActivity() {

    private lateinit var viewModel: CreateSellingEnquiryViewModel

    private lateinit var btnBack: ImageView
    private lateinit var tvStepCount: TextView
    private lateinit var tvCustomerTab: TextView
    private lateinit var tvEnquiryTab: TextView

    private lateinit var customerIndicator: View
    private lateinit var enquiryIndicator: View

    private var customerFragment: CustomerDetailsFragment? = null
    private var enquiryFragment: EnquiryDetailsFragment? = null

    private var currentStep = 1
    private var isSubmitting = false

    companion object {
        private const val CUSTOMER_TAG = "customer_details"
        private const val ENQUIRY_TAG = "enquiry_details"
        private const val KEY_CURRENT_STEP = "current_step"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_selling_enquiry)

        initializeViews()
        initializeViewModel()
        initializeFragments(savedInstanceState)
        setupListeners()
        observeCreateEnquiry()
        updateTabUI()
    }

    private fun initializeViews() {

        btnBack = findViewById(R.id.btnBack)
        tvStepCount = findViewById(R.id.tvStepCount)

        tvCustomerTab = findViewById(R.id.tvCustomerTab)
        tvEnquiryTab = findViewById(R.id.tvEnquiryTab)

        customerIndicator = findViewById(R.id.customerIndicator)
        enquiryIndicator = findViewById(R.id.enquiryIndicator)
    }

    private fun initializeViewModel() {

        viewModel = ViewModelProvider(
            this,
            CreateSellingEnquiryViewModelFactory(applicationContext)
        )[CreateSellingEnquiryViewModel::class.java]
    }

    private fun initializeFragments(savedInstanceState: Bundle?) {

        if (savedInstanceState == null) {

            customerFragment = CustomerDetailsFragment()
            enquiryFragment = EnquiryDetailsFragment()

            supportFragmentManager.beginTransaction()
                .add(
                    R.id.fragmentContainer,
                    customerFragment!!,
                    CUSTOMER_TAG
                )
                .add(
                    R.id.fragmentContainer,
                    enquiryFragment!!,
                    ENQUIRY_TAG
                )
                .hide(enquiryFragment!!)
                .commit()

            currentStep = 1

        } else {

            customerFragment = supportFragmentManager
                .findFragmentByTag(CUSTOMER_TAG) as? CustomerDetailsFragment

            enquiryFragment = supportFragmentManager
                .findFragmentByTag(ENQUIRY_TAG) as? EnquiryDetailsFragment

            currentStep = savedInstanceState.getInt(KEY_CURRENT_STEP, 1)
        }
    }

    private fun setupListeners() {

        btnBack.setOnClickListener {
            if (currentStep == 2) {
                navigateToCustomer()
            } else {
                finish()
            }
        }

        tvCustomerTab.setOnClickListener {
            if (currentStep == 2) {
                navigateToCustomer()
            }
        }

        tvEnquiryTab.setOnClickListener {
            if (currentStep == 1) {

                val customerData =
                    customerFragment?.validateAndGetData()

                if (customerData != null) {
                    navigateToEnquiry()
                }
            }
        }
    }

    fun navigateToEnquiry() {

        val customer = customerFragment?.validateAndGetData() ?: return
        currentStep = 2

        supportFragmentManager.beginTransaction()
            .hide(customerFragment!!)
            .show(enquiryFragment!!)
            .commit()

        updateTabUI()
    }

    fun navigateToCustomer() {

        currentStep = 1

        supportFragmentManager.beginTransaction()
            .hide(enquiryFragment!!)
            .show(customerFragment!!)
            .commit()

        updateTabUI()
    }

    private fun updateTabUI() {

        tvStepCount.text =
            if (currentStep == 1) "01/02" else "02/02"

        val selectedColor = getColor(R.color.selling_navy)
        val unselectedColor = getColor(R.color.selling_tab_unselected)

        if (currentStep == 1) {

            tvCustomerTab.setTextColor(selectedColor)
            tvEnquiryTab.setTextColor(unselectedColor)

            tvCustomerTab.setTypeface(null, android.graphics.Typeface.BOLD)
            tvEnquiryTab.setTypeface(null, android.graphics.Typeface.NORMAL)

            customerIndicator.setBackgroundColor(selectedColor)
            enquiryIndicator.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        } else {

            tvCustomerTab.setTextColor(unselectedColor)
            tvEnquiryTab.setTextColor(selectedColor)

            tvCustomerTab.setTypeface(null, android.graphics.Typeface.NORMAL)
            tvEnquiryTab.setTypeface(null, android.graphics.Typeface.BOLD)

            customerIndicator.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            enquiryIndicator.setBackgroundColor(selectedColor)
        }
    }

    fun submitEnquiry() {

        if (isSubmitting) return

        val customerData: CustomerDetails =
            customerFragment?.validateAndGetData() ?: return

        val enquiryData: EnquiryDetails =
            enquiryFragment?.validateAndGetData() ?: return

        val request = CreateSellingEnquiryRequest(
            mobile = customerData.mobile,
            customerName = customerData.customerName,
            customerType = customerData.customerType,
            gender = customerData.gender,
            email = customerData.email,
            state = customerData.state,
            district = customerData.district,
            city = customerData.city,
            address = customerData.address,
            pinCode = customerData.pinCode,
            whatsappNo = customerData.whatsappNo,

            enquiryNo = enquiryData.enquiryNo,
            enquiryDate = enquiryData.enquiryDate,
            enquirySource = enquiryData.enquirySource,
            enquirySubSource = enquiryData.enquirySubSource,
            maker = enquiryData.maker,
            model = enquiryData.model,
            variant = enquiryData.variant,
            fuelType = enquiryData.fuelType,
            registrationNo = enquiryData.registrationNo
        )

        val token = getAuthToken()

        if (token.isBlank()) {
            Toast.makeText(
                this,
                "Login token not found",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.createSellingEnquiry(
            token = token,
            request = request
        )
    }

    private fun getAuthToken(): String {
        // Replace this with your existing PreferenceProvider token method.
        return getSharedPreferences("login", MODE_PRIVATE)
            .getString("token", "")
            .orEmpty()
    }

    private fun observeCreateEnquiry() {

        viewModel.createSellingEnquiryResponse.observe(this) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    isSubmitting = true
                    enquiryFragment?.setCreateLoading(true)
                }

                ApiStatus.Status.SUCCESS -> {

                    isSubmitting = false
                    enquiryFragment?.setCreateLoading(false)

                    Toast.makeText(
                        this,
                        resource.data?.message
                            ?: "Enquiry created successfully",
                        Toast.LENGTH_LONG
                    ).show()

                    setResult(RESULT_OK)
                    finish()
                }

                ApiStatus.Status.ERROR -> {

                    isSubmitting = false
                    enquiryFragment?.setCreateLoading(false)

                    Toast.makeText(
                        this,
                        resource.message
                            ?: "Unable to create enquiry",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_CURRENT_STEP, currentStep)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentStep == 2) {
            navigateToCustomer()
        } else {
            super.onBackPressed()
        }
    }
}