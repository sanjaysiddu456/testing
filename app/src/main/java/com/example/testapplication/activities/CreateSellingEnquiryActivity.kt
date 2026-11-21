
package com.example.testapplication.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.testapplication.R
import com.example.testapplication.databinding.ActivityCreateSellingEnquiryBinding

import com.example.testapplication.datamodels.CreateSellingEnquiryRequest
import com.example.testapplication.datamodels.UpdateSellingEnquiryRequest
import com.example.testapplication.fragments.CustomerDetails
import com.example.testapplication.fragments.CustomerDetailsFragment
import com.example.testapplication.fragments.EnquiryDetails
import com.example.testapplication.fragments.EnquiryDetailsFragment
import com.example.testapplication.utils.ApiStatus
import com.example.testapplication.viewmodels.CreateSellingEnquiryViewModel
import com.example.testapplication.viewmodels.CreateSellingEnquiryViewModelFactory

class CreateSellingEnquiryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateSellingEnquiryBinding

    private lateinit var viewModel: CreateSellingEnquiryViewModel

    private var customerFragment: CustomerDetailsFragment? = null
    private var enquiryFragment: EnquiryDetailsFragment? = null

    private var currentStep = 1
    private var isSubmitting = false
    private var currentMode = MODE_CREATE
    private var enquiryId = -1
    private var authToken = ""

    companion object {
        private const val CUSTOMER_TAG = "customer_details"
        private const val ENQUIRY_TAG = "enquiry_details"
        private const val KEY_CURRENT_STEP = "current_step"


            const val EXTRA_MODE = "MODE"
            const val EXTRA_ENQUIRY_ID = "ENQUIRY_ID"
            const val EXTRA_TOKEN = "TOKEN"

            const val MODE_CREATE = "CREATE"
            const val MODE_EDIT = "EDIT"

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateSellingEnquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readIntentValues()

        if (isFinishing) return

        initializeViewModel()

        observeCreateEnquiry()
        observeEnquiryDetails()
        observeUpdateResponse()

        initializeFragments()

        setupListeners()

        updateTabUI()

    }



    private fun readIntentValues() {
        currentMode = intent.getStringExtra(EXTRA_MODE) ?: MODE_CREATE

        enquiryId = intent.getIntExtra(EXTRA_ENQUIRY_ID, -1)

        authToken = intent.getStringExtra(EXTRA_TOKEN).orEmpty()

        if (currentMode == MODE_EDIT && enquiryId == -1) {
            Toast.makeText(
                this,
                "Invalid enquiry ID",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }

    private fun initializeViewModel() {

        viewModel = ViewModelProvider(
            this,
            CreateSellingEnquiryViewModelFactory(applicationContext)
        )[CreateSellingEnquiryViewModel::class.java]
    }

    private fun initializeFragments() {

        val fragmentManager = supportFragmentManager

        customerFragment =
            fragmentManager.findFragmentByTag("customer_details")
                    as? CustomerDetailsFragment

        enquiryFragment =
            fragmentManager.findFragmentByTag("enquiry_details")
                    as? EnquiryDetailsFragment

        if (customerFragment == null || enquiryFragment == null) {

            customerFragment = CustomerDetailsFragment()
            enquiryFragment = EnquiryDetailsFragment()

            fragmentManager.beginTransaction()
                .add(
                    R.id.fragmentContainer,
                    customerFragment!!,
                    "customer_details"
                )
                .add(
                    R.id.fragmentContainer,
                    enquiryFragment!!,
                    "enquiry_details"
                )
                .hide(enquiryFragment!!)
                .commitNow()
        }

        if (currentMode == MODE_EDIT) {
            enquiryFragment?.setButtonText("Update")
            loadExistingEnquiry()
        } else {
            enquiryFragment?.setButtonText("Create")
        }
    }

    private fun loadExistingEnquiry() {
        if (enquiryId <= 0 || authToken.isBlank()) {
            Toast.makeText(
                this,
                "Invalid enquiry details",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        viewModel.getEnquiryDetails(
            enquiryId = enquiryId,
            token = authToken
        )
    }

    private fun setupListeners() {

        binding.btnBack.setOnClickListener {

            if (currentStep == 2) {
                navigateToCustomer()
            } else {
                finish()
            }
        }

        binding.tvCustomerTab.setOnClickListener {

            if (currentStep == 2) {
                navigateToCustomer()
            }
        }

        binding.tvEnquiryTab.setOnClickListener {

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

        val customerFrag = customerFragment ?: return
        val enquiryFrag = enquiryFragment ?: return

        currentStep = 2

        supportFragmentManager.beginTransaction()
            .hide(customerFrag)
            .show(enquiryFrag)
            .commit()

        updateTabUI()
    }

    fun navigateToCustomer() {

        val customerFrag = customerFragment ?: return
        val enquiryFrag = enquiryFragment ?: return

        currentStep = 1

        supportFragmentManager.beginTransaction()
            .hide(enquiryFrag)
            .show(customerFrag)
            .commit()

        updateTabUI()
    }

    private fun updateTabUI() {

        binding.tvStepCount.text =
            if (currentStep == 1) "01/02" else "02/02"

        val selectedColor = getColor(R.color.selling_navy)
        val unselectedColor = getColor(R.color.selling_tab_unselected)

        if (currentStep == 1) {

            binding.tvCustomerTab.setTextColor(selectedColor)
            binding.tvEnquiryTab.setTextColor(unselectedColor)

            binding.tvCustomerTab.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            binding.tvEnquiryTab.setTypeface(
                null,
                android.graphics.Typeface.NORMAL
            )

            binding.customerIndicator.setBackgroundColor(selectedColor)

            binding.enquiryIndicator.setBackgroundColor(
                android.graphics.Color.TRANSPARENT
            )

        } else {

            binding.tvCustomerTab.setTextColor(unselectedColor)
            binding.tvEnquiryTab.setTextColor(selectedColor)

            binding.tvCustomerTab.setTypeface(
                null,
                android.graphics.Typeface.NORMAL
            )

            binding.tvEnquiryTab.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            binding.customerIndicator.setBackgroundColor(
                android.graphics.Color.TRANSPARENT
            )

            binding.enquiryIndicator.setBackgroundColor(selectedColor)
        }
    }

    private fun submitUpdateEnquiry(
        customer: CustomerDetails,
        enquiry: EnquiryDetails
    ) {

        val request = UpdateSellingEnquiryRequest(
            mobile = customer.mobile,
            customerName = customer.customerName,
            customerType = customer.customerType,
            gender = customer.gender,
            email = customer.email,
            state = customer.state,
            district = customer.district,
            city = customer.city,
            address = customer.address,
            pinCode = customer.pinCode,
            whatsappNo = customer.whatsappNo,

            enquiryNo = enquiry.enquiryNo,
            enquiryDate = enquiry.enquiryDate,
            enquirySource = enquiry.enquirySource,
            enquirySubSource = enquiry.enquirySubSource,
            maker = enquiry.maker,
            model = enquiry.model,
            variant = enquiry.variant,
            fuelType = enquiry.fuelType,
            registrationNo = enquiry.registrationNo
        )

        viewModel.updateSellingEnquiry(
            enquiryId = enquiryId,
            token = authToken,
            request = request
        )
    }

     fun submitEnquiry() {

        val customer = customerFragment?.getCustomerDetails()
            ?: return

        val enquiry = enquiryFragment?.getEnquiryDetails()
            ?: return

        when (currentMode) {

            MODE_CREATE -> {
                submitCreateEnquiry(customer, enquiry)
            }

            MODE_EDIT -> {
                submitUpdateEnquiry(customer, enquiry)
            }
        }
    }

    private fun observeUpdateResponse() {

        viewModel.updateEnquiryResponse.observe(this) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {
                    enquiryFragment?.setCreateLoading(true)
                }

                ApiStatus.Status.SUCCESS -> {
                    enquiryFragment?.setCreateLoading(false)

                    Toast.makeText(
                        this,
                        resource.data?.message ?: "Enquiry updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    setResult(RESULT_OK)
                    finish()
                }

                ApiStatus.Status.ERROR -> {
                    enquiryFragment?.setCreateLoading(false)

                    Toast.makeText(
                        this,
                        resource.message ?: "Unable to update enquiry",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun submitCreateEnquiry(
        customer: CustomerDetails,
        enquiry: EnquiryDetails
    ) {
        val request = CreateSellingEnquiryRequest(
            mobile = customer.mobile,
            customerName = customer.customerName,
            customerType = customer.customerType,
            gender = customer.gender,
            email = customer.email,
            state = customer.state,
            district = customer.district,
            city = customer.city,
            address = customer.address,
            pinCode = customer.pinCode,
            whatsappNo = customer.whatsappNo,
            enquiryNo = enquiry.enquiryNo,
            enquiryDate = enquiry.enquiryDate,
            enquirySource = enquiry.enquirySource,
            enquirySubSource = enquiry.enquirySubSource,
            maker = enquiry.maker,
            model = enquiry.model,
            variant = enquiry.variant,
            fuelType = enquiry.fuelType,
            registrationNo = enquiry.registrationNo
        )

        // Call your EXISTING create API here.
        // Keep your current create ViewModel, observer and success handling.
    }

    private fun getAuthToken(): String {

        // Replace with your existing PreferenceProvider token method.
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

    private fun observeEnquiryDetails() {

        viewModel.enquiryDetailsResponse.observe(this) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {
                    // Show your existing loader here.
                }

                ApiStatus.Status.SUCCESS -> {
                    // Hide your existing loader here.

                    val enquiry = resource.data?.data

                    if (enquiry != null) {
                        customerFragment?.populateCustomerDetails(enquiry)
                        enquiryFragment?.populateEnquiryDetails(enquiry)
                    }
                }

                ApiStatus.Status.ERROR -> {
                    // Hide your existing loader here.

                    Toast.makeText(
                        this,
                        resource.message ?: "Unable to load enquiry details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
