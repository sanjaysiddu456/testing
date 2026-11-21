
package com.example.testapplication.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testapplication.activities.CreateSellingEnquiryActivity
import com.example.testapplication.adapters.CustomSpinnerAdapter
import com.example.testapplication.databinding.FragmentCustomerDetailsBinding
import com.example.testapplication.datamodels.Enquiry

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

    private var _binding: FragmentCustomerDetailsBinding? = null

    private var lastSearchedPinCode = ""
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCustomerDetailsBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupPinCodeSearch()
        observePinCodeResponse()

        binding.btnContinue.setOnClickListener {

            val customer = validateAndGetData()

            if (customer != null) {
                (requireActivity() as CreateSellingEnquiryActivity)
                    .navigateToEnquiry()
            }
        }
    }
    private fun setupPinCodeSearch() {

        binding.etPinCode.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val pinCode = s.toString().trim()

                    if (pinCode.length < 6) {
                        lastSearchedPinCode = ""

                        binding.etState.setText("")
                        binding.etDistrict.setText("")
                        binding.etCity.setText("")
                        return
                    }

                    if (
                        pinCode.length == 6 &&
                        pinCode != lastSearchedPinCode
                    ) {
                        lastSearchedPinCode = pinCode

                        pinCodeViewModel.searchPinCode(
                            Constants.LOGIN_TOKEN,
                            pinCode
                        )
                    }
                }

                override fun afterTextChanged(s: Editable?) = Unit
            }
        )
    }

    private fun observePinCodeResponse() {

        pinCodeViewModel.pinCodeResponse.observe(
            viewLifecycleOwner
        ) { resource ->

            val response = resource.data ?: return@observe

            val item = response.getData()
                ?.firstOrNull()
                ?: return@observe

            val json = Gson().toJsonTree(item).asJsonObject

            fun getValue(vararg keys: String): String {
                for (key in keys) {
                    if (json.has(key) && !json.get(key).isJsonNull) {
                        return json.get(key).asString
                    }
                }
                return ""
            }

            val state = getValue(
                "sStcdName",
                "SStcdName",
                "stateName",
                "state"
            )

            val district = getValue(
                "sDstrtName",
                "SDstrtName",
                "districtName",
                "district"
            )

            val city = getValue(
                "sCityName",
                "SCityName",
                "cityName",
                "city"
            )

            val postOffice = getValue(
                "sPostOfficeName",
                "SPostOfficeName",
                "postOfficeName",
                "postOffice"
            )

            binding.etState.setText(state)
            binding.etDistrict.setText(district)
            binding.etCity.setText(city)

            // Do not overwrite the customer's manually entered address.
            if (binding.etAddress.text.isNullOrBlank()) {
                binding.etAddress.setText(postOffice)
            }
        }
    }
    private fun setupSpinners() {

        setSpinner(
            binding.spCustomerType,
            listOf(
                "Select Customer Type",
                "Individual",
                "Corporate",
                "Dealer"
            )
        )

        setSpinner(
            binding.spGender,
            listOf(
                "Select Gender",
                "Male",
                "Female",
                "Other"
            )
        )

        setSpinner(
            binding.spState,
            listOf(
                "Select State",
                "Karnataka",
                "Tamil Nadu",
                "Kerala",
                "Maharashtra"
            )
        )

        setSpinner(
            binding.spDistrict,
            listOf(
                "Select District",
                "Bengaluru Urban",
                "Mysuru",
                "Mandya",
                "Tumakuru"
            )
        )

        setSpinner(
            binding.spCity,
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
        spinner: android.widget.Spinner,
        values: List<String>
    ) {
        spinner.adapter = CustomSpinnerAdapter(
            requireContext(),
            values
        )

        spinner.setSelection(0)
    }

    fun validateAndGetData(): CustomerDetails? {

        val mobile = binding.etMobile.text.toString().trim()
        val name = binding.etCustomerName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val pinCode = binding.etPinCode.text.toString().trim()
        val whatsapp = binding.etWhatsapp.text.toString().trim()

        if (mobile.length != 10) {
            binding.etMobile.error = "Enter valid 10 digit mobile number"
            binding.etMobile.requestFocus()
            return null
        }

        if (name.isEmpty()) {
            binding.etCustomerName.error = "Enter customer name"
            binding.etCustomerName.requestFocus()
            return null
        }

        if (binding.spCustomerType.selectedItemPosition == 0) {
            showMessage("Select customer type")
            return null
        }

        if (binding.spGender.selectedItemPosition == 0) {
            showMessage("Select gender")
            return null
        }

        if (email.isNotEmpty() &&
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            binding.etEmail.error = "Enter valid email"
            binding.etEmail.requestFocus()
            return null
        }

        val state = binding.etState.text.toString().trim()
        val district = binding.etDistrict.text.toString().trim()
        val city = binding.etCity.text.toString().trim()

        if (state.isEmpty()) {
            binding.etState.error = "Enter state"
            return null
        }

        if (district.isEmpty()) {
            binding.etDistrict.error = "Enter district"
            return null
        }

        if (city.isEmpty()) {
            binding.etCity.error = "Enter city"
            return null
        }

        if (address.isEmpty()) {
            binding.etAddress.error = "Enter address"
            binding.etAddress.requestFocus()
            return null
        }

        if (pinCode.isNotEmpty() && pinCode.length != 6) {
            binding.etPinCode.error = "Enter valid pincode"
            binding.etPinCode.requestFocus()
            return null
        }

        if (whatsapp.isNotEmpty() && whatsapp.length != 10) {
            binding.etWhatsapp.error = "Enter valid WhatsApp number"
            binding.etWhatsapp.requestFocus()
            return null
        }

        return CustomerDetails(
            mobile = mobile,
            customerName = name,
            customerType = binding.spCustomerType.selectedItem.toString(),
            gender = binding.spGender.selectedItem.toString(),
            email = email,
            state = state,
            district = district,
            city = city,
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


    fun populateCustomerDetails(enquiry: Enquiry) {
        if (_binding == null) return

        binding.apply {
            etMobile.setText(enquiry.mobileNumber)
            etCustomerName.setText(enquiry.customerName)
            etEmail.setText(enquiry.email)
            etAddress.setText(enquiry.address)
            etPinCode.setText(enquiry.pinCode)
            etWhatsapp.setText(enquiry.whatsappNo)

            setSpinnerValue(spCustomerType, enquiry.customerType)
            setSpinnerValue(spGender, enquiry.gender)
            etState.setText(enquiry.state)
            etDistrict.setText(enquiry.district)
            etCity.setText(enquiry.city)
        }
    }
    fun getCustomerDetails(): CustomerDetails? {
        if (_binding == null) return null

        return validateAndGetData()
    }
    private fun setSpinnerValue(
        spinner: Spinner,
        value: String
    ) {
        if (value.isBlank()) return

        val adapter = spinner.adapter ?: return

        for (position in 0 until adapter.count) {
            if (
                adapter.getItem(position)?.toString()
                    ?.equals(value, ignoreCase = true) == true
            ) {
                spinner.setSelection(position)
                return
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
