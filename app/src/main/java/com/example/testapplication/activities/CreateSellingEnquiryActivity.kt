
package com.example.testapplication.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.testapplication.R

import com.example.testapplication.datamodels.CreateSellingEnquiryRequest
import com.example.testapplication.fragments.CustomerDetails
import com.example.testapplication.fragments.CustomerDetailsFragment
import com.example.testapplication.fragments.EnquiryDetails
import com.example.testapplication.fragments.EnquiryDetailsFragment
import com.example.testapplication.viewmodels.CreateSellingEnquiryViewModel
import com.example.testapplication.viewmodels.CreateSellingEnquiryViewModelFactory

class CreateSellingEnquiryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateSellingEnquiryBinding

    private lateinit var viewModel: CreateSellingEnquiryViewModel

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

        binding = ActivityCreateSellingEnquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViewModel()
        initializeFragments(savedInstanceState)
        setupListeners()
        observeCreateEnquiry()
        updateTabUI()
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (currentStep == 2) {
            navigateToCustomer()
        } else {
            super.onBackPressed()
        }
    }
}
