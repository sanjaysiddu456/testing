package com.example.testapplication.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testapplication.R
import com.example.testapplication.activities.CreateSellingEnquiryActivity
import com.example.testapplication.adapters.CustomSpinnerAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class EnquiryDetails(
    val enquiryNo: String,
    val enquiryDate: String,
    val enquirySource: String,
    val enquirySubSource: String,
    val maker: String,
    val model: String,
    val variant: String,
    val fuelType: String,
    val registrationNo: String
)

class EnquiryDetailsFragment : Fragment() {

    private lateinit var etEnquiryNo: EditText
    private lateinit var etEnquiryDate: EditText
    private lateinit var etRegistrationNo: EditText

    private lateinit var spEnquirySource: Spinner
    private lateinit var spEnquirySubSource: Spinner
    private lateinit var spMaker: Spinner
    private lateinit var spModel: Spinner
    private lateinit var spVariant: Spinner
    private lateinit var spFuelType: Spinner

    private lateinit var btnCreate: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_enquiry_details,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupSpinners()

        etEnquiryDate.setOnClickListener {
            showDatePicker()
        }

        btnCreate.setOnClickListener {
            val enquiry = validateAndGetData()

            if (enquiry != null) {
                (requireActivity() as CreateSellingEnquiryActivity)
                    .submitEnquiry()
            }
        }
    }

    private fun initializeViews(view: View) {

        etEnquiryNo = view.findViewById(R.id.etEnquiryNo)
        etEnquiryDate = view.findViewById(R.id.etEnquiryDate)
        etRegistrationNo = view.findViewById(R.id.etRegistrationNo)

        spEnquirySource = view.findViewById(R.id.spEnquirySource)
        spEnquirySubSource = view.findViewById(R.id.spEnquirySubSource)
        spMaker = view.findViewById(R.id.spMaker)
        spModel = view.findViewById(R.id.spModel)
        spVariant = view.findViewById(R.id.spVariant)
        spFuelType = view.findViewById(R.id.spFuelType)

        btnCreate = view.findViewById(R.id.btnCreate)
    }

    private fun setupSpinners() {

        setSpinner(
            spEnquirySource,
            listOf(
                "Select Source",
                "Walk-in",
                "Website",
                "Referral",
                "Campaign"
            )
        )

        setSpinner(
            spEnquirySubSource,
            listOf(
                "Select Sub Source",
                "Google",
                "Facebook",
                "Existing Customer",
                "Other"
            )
        )

        setSpinner(
            spMaker,
            listOf(
                "Select Maker",
                "Hyundai",
                "Maruti Suzuki",
                "Tata",
                "Kia"
            )
        )

        setSpinner(
            spModel,
            listOf(
                "Select Model",
                "Creta",
                "Venue",
                "i20",
                "Exter"
            )
        )

        setSpinner(
            spVariant,
            listOf(
                "Select Variant",
                "Base",
                "Mid",
                "Top"
            )
        )

        setSpinner(
            spFuelType,
            listOf(
                "Select Fuel Type",
                "Petrol",
                "Diesel",
                "CNG",
                "Electric"
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

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->

                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }

                val formattedDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(selectedDate.time)

                etEnquiryDate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun validateAndGetData(): EnquiryDetails? {

        val enquiryNo = etEnquiryNo.text.toString().trim()
        val enquiryDate = etEnquiryDate.text.toString().trim()
        val registrationNo = etRegistrationNo.text.toString().trim()

        if (enquiryNo.isEmpty()) {
            etEnquiryNo.error = "Enter enquiry number"
            etEnquiryNo.requestFocus()
            return null
        }

        if (enquiryDate.isEmpty()) {
            etEnquiryDate.error = "Select enquiry date"
            etEnquiryDate.requestFocus()
            return null
        }

        if (spMaker.selectedItemPosition == 0) {
            showMessage("Select maker")
            return null
        }

        if (spModel.selectedItemPosition == 0) {
            showMessage("Select model")
            return null
        }

        if (spFuelType.selectedItemPosition == 0) {
            showMessage("Select fuel type")
            return null
        }

        return EnquiryDetails(
            enquiryNo = enquiryNo,
            enquiryDate = enquiryDate,
            enquirySource = getSelectedValue(spEnquirySource),
            enquirySubSource = getSelectedValue(spEnquirySubSource),
            maker = spMaker.selectedItem.toString(),
            model = spModel.selectedItem.toString(),
            variant = getSelectedValue(spVariant),
            fuelType = spFuelType.selectedItem.toString(),
            registrationNo = registrationNo
        )
    }

    private fun getSelectedValue(spinner: Spinner): String {
        return if (spinner.selectedItemPosition == 0) {
            ""
        } else {
            spinner.selectedItem.toString()
        }
    }

    fun setCreateLoading(isLoading: Boolean) {
        if (!::btnCreate.isInitialized) return

        btnCreate.isEnabled = !isLoading
        btnCreate.text = if (isLoading) "Creating..." else "Create"
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}