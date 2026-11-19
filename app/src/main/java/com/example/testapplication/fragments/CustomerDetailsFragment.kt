package com.example.testapplication.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testapplication.activities.CreateSellingEnquiryActivity
import com.example.testapplication.R
import com.example.testapplication.adapters.CustomSpinnerAdapter

data class CustomerDetails(
    val mobile: String,
    val customerName: String,
    val customerType: String,
    val gender: String,
    val email: String,
    val state: String,
    val district: String,
    val city: String,
    val address: String,
    val pinCode: String,
    val whatsappNo: String
)

class CustomerDetailsFragment : Fragment() {

    private lateinit var etMobile: EditText
    private lateinit var etCustomerName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPinCode: EditText
    private lateinit var etWhatsapp: EditText

    private lateinit var spCustomerType: Spinner
    private lateinit var spGender: Spinner
    private lateinit var spState: Spinner
    private lateinit var spDistrict: Spinner
    private lateinit var spCity: Spinner

    private lateinit var btnContinue: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_customer_details,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupSpinners()

        btnContinue.setOnClickListener {
            val customer = validateAndGetData()

            if (customer != null) {
                (requireActivity() as CreateSellingEnquiryActivity)
                    .navigateToEnquiry()
            }
        }
    }

    private fun initializeViews(view: View) {

        etMobile = view.findViewById(R.id.etMobile)
        etCustomerName = view.findViewById(R.id.etCustomerName)
        etEmail = view.findViewById(R.id.etEmail)
        etAddress = view.findViewById(R.id.etAddress)
        etPinCode = view.findViewById(R.id.etPinCode)
        etWhatsapp = view.findViewById(R.id.etWhatsapp)

        spCustomerType = view.findViewById(R.id.spCustomerType)
        spGender = view.findViewById(R.id.spGender)
        spState = view.findViewById(R.id.spState)
        spDistrict = view.findViewById(R.id.spDistrict)
        spCity = view.findViewById(R.id.spCity)

        btnContinue = view.findViewById(R.id.btnContinue)
    }

    private fun setupSpinners() {

        setSpinner(
            spCustomerType,
            listOf(
                "Select Customer Type",
                "Individual",
                "Corporate",
                "Dealer"
            )
        )

        setSpinner(
            spGender,
            listOf(
                "Select Gender",
                "Male",
                "Female",
                "Other"
            )
        )

        setSpinner(
            spState,
            listOf(
                "Select State",
                "Karnataka",
                "Tamil Nadu",
                "Kerala",
                "Maharashtra"
            )
        )

        setSpinner(
            spDistrict,
            listOf(
                "Select District",
                "Bengaluru Urban",
                "Mysuru",
                "Mandya",
                "Tumakuru"
            )
        )

        setSpinner(
            spCity,
            listOf(
                "Select City",
                "Bengaluru",
                "Mysuru",
                "Mandya",
                "Tumakuru"
            )
        )
    }

    private fun setSpinner(
        spinner: Spinner,
        values: List<String>
    ) {
        spinner.adapter = CustomSpinnerAdapter(
            requireContext(),
            values
        )
        spinner.setSelection(0)
    }

    fun validateAndGetData(): CustomerDetails? {

        val mobile = etMobile.text.toString().trim()
        val name = etCustomerName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val pinCode = etPinCode.text.toString().trim()
        val whatsapp = etWhatsapp.text.toString().trim()

        if (mobile.length != 10) {
            etMobile.error = "Enter valid 10 digit mobile number"
            etMobile.requestFocus()
            return null
        }

        if (name.isEmpty()) {
            etCustomerName.error = "Enter customer name"
            etCustomerName.requestFocus()
            return null
        }

        if (spCustomerType.selectedItemPosition == 0) {
            showMessage("Select customer type")
            return null
        }

        if (spGender.selectedItemPosition == 0) {
            showMessage("Select gender")
            return null
        }

        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter valid email"
            etEmail.requestFocus()
            return null
        }

        if (spState.selectedItemPosition == 0) {
            showMessage("Select state")
            return null
        }

        if (spDistrict.selectedItemPosition == 0) {
            showMessage("Select district")
            return null
        }

        if (spCity.selectedItemPosition == 0) {
            showMessage("Select city")
            return null
        }

        if (address.isEmpty()) {
            etAddress.error = "Enter address"
            etAddress.requestFocus()
            return null
        }

        if (pinCode.isNotEmpty() && pinCode.length != 6) {
            etPinCode.error = "Enter valid pincode"
            etPinCode.requestFocus()
            return null
        }

        if (whatsapp.isNotEmpty() && whatsapp.length != 10) {
            etWhatsapp.error = "Enter valid WhatsApp number"
            etWhatsapp.requestFocus()
            return null
        }

        return CustomerDetails(
            mobile = mobile,
            customerName = name,
            customerType = spCustomerType.selectedItem.toString(),
            gender = spGender.selectedItem.toString(),
            email = email,
            state = spState.selectedItem.toString(),
            district = spDistrict.selectedItem.toString(),
            city = spCity.selectedItem.toString(),
            address = address,
            pinCode = pinCode,
            whatsappNo = whatsapp
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}