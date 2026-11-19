
package com.example.testapplication.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testapplication.activities.CreateSellingEnquiryActivity
import com.example.testapplication.adapters.CustomSpinnerAdapter
import com.example.testapplication.databinding.FragmentCustomerDetailsBinding

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

        binding.btnContinue.setOnClickListener {

            val customer = validateAndGetData()

            if (customer != null) {
                (requireActivity() as CreateSellingEnquiryActivity)
                    .navigateToEnquiry()
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

        if (binding.spState.selectedItemPosition == 0) {
            showMessage("Select state")
            return null
        }

        if (binding.spDistrict.selectedItemPosition == 0) {
            showMessage("Select district")
            return null
        }

        if (binding.spCity.selectedItemPosition == 0) {
            showMessage("Select city")
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
            state = binding.spState.selectedItem.toString(),
            district = binding.spDistrict.selectedItem.toString(),
            city = binding.spCity.selectedItem.toString(),
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
