
package com.example.testapplication.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testapplication.activities.CreateSellingEnquiryActivity
import com.example.testapplication.adapters.CustomSpinnerAdapter
import com.example.testapplication.databinding.FragmentEnquiryDetailsBinding
import com.example.testapplication.datamodels.Enquiry
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

    private var _binding: FragmentEnquiryDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnquiryDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()

        binding.etEnquiryDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnCreate.setOnClickListener {

            val enquiry = validateAndGetData()

            if (enquiry != null) {
                (requireActivity() as CreateSellingEnquiryActivity)
                    .submitEnquiry()
            }
        }
    }

    private fun setMakerList(
        masterData: HPromiseSalesMasterData
    ) {
        val makerNames = mutableListOf<String>()

        makerNames.add("Select Maker")

        val makerList = masterData.getMakerList()

        if (!makerList.isNullOrEmpty()) {
            makerList.forEach { maker ->
                makerNames.add(maker.getCmCodeName())
            }
        }

        val adapter = CustomSpinnerAdapter(
            requireContext(),
            makerNames
        )

        binding.spMaker.adapter = adapter
        binding.spMaker.setSelection(0)

        binding.spMaker.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        val selectedMaker = makerList[position - 1]

                        val makerId = selectedMaker.getCmCode()

                        // Later we will use makerId to load models.
                    }
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {}
            }
    }

    private fun setupSpinners() {

        setSpinner(
            binding.spEnquirySource,
            listOf(
                "Select Source",
                "Walk-in",
                "Website",
                "Referral",
                "Campaign"
            )
        )

        setSpinner(
            binding.spEnquirySubSource,
            listOf(
                "Select Sub Source",
                "Google",
                "Facebook",
                "Existing Customer",
                "Other"
            )
        )

        setSpinner(
            binding.spMaker,
            listOf(
                "Select Maker",
                "Hyundai",
                "Maruti Suzuki",
                "Tata",
                "Kia"
            )
        )

        setSpinner(
            binding.spModel,
            listOf(
                "Select Model",
                "Creta",
                "Venue",
                "i20",
                "Exter"
            )
        )

        setSpinner(
            binding.spVariant,
            listOf(
                "Select Variant",
                "Base",
                "Mid",
                "Top"
            )
        )

        setSpinner(
            binding.spFuelType,
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
        spinner: android.widget.Spinner,
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

                binding.etEnquiryDate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun validateAndGetData(): EnquiryDetails? {

        val enquiryNo = binding.etEnquiryNo.text.toString().trim()
        val enquiryDate = binding.etEnquiryDate.text.toString().trim()
        val registrationNo = binding.etRegistrationNo.text.toString().trim()

        if (enquiryNo.isEmpty()) {
            binding.etEnquiryNo.error = "Enter enquiry number"
            binding.etEnquiryNo.requestFocus()
            return null
        }

        if (enquiryDate.isEmpty()) {
            binding.etEnquiryDate.error = "Select enquiry date"
            binding.etEnquiryDate.requestFocus()
            return null
        }

        if (binding.spMaker.selectedItemPosition == 0) {
            showMessage("Select maker")
            return null
        }

        if (binding.spModel.selectedItemPosition == 0) {
            showMessage("Select model")
            return null
        }

        if (binding.spFuelType.selectedItemPosition == 0) {
            showMessage("Select fuel type")
            return null
        }

        return EnquiryDetails(
            enquiryNo = enquiryNo,
            enquiryDate = enquiryDate,
            enquirySource = getSelectedValue(binding.spEnquirySource),
            enquirySubSource = getSelectedValue(binding.spEnquirySubSource),
            maker = binding.spMaker.selectedItem.toString(),
            model = binding.spModel.selectedItem.toString(),
            variant = getSelectedValue(binding.spVariant),
            fuelType = binding.spFuelType.selectedItem.toString(),
            registrationNo = registrationNo
        )
    }

    private fun getSelectedValue(
        spinner: android.widget.Spinner
    ): String {
        return if (spinner.selectedItemPosition == 0) {
            ""
        } else {
            spinner.selectedItem.toString()
        }
    }

    fun populateEnquiryDetails(enquiry: Enquiry) {
        if (_binding == null) return

        binding.apply {
            etEnquiryNo.setText(enquiry.enquiryNo)
            etEnquiryDate.setText(enquiry.dateOfEnquiry)
            etRegistrationNo.setText(enquiry.regNo)

            setSpinnerValue(spEnquirySource, enquiry.enquirySource)
            setSpinnerValue(spEnquirySubSource, enquiry.enquirySubSource)
            setSpinnerValue(spMaker, enquiry.maker)
            setSpinnerValue(spModel, enquiry.model)
            setSpinnerValue(spVariant, enquiry.variant)
            setSpinnerValue(spFuelType, enquiry.fuelType)
        }
    }

    fun getEnquiryDetails(): EnquiryDetails? {
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
    fun setButtonText(text: String) {
        _binding?.let { binding ->
            binding.btnCreate.tag = text
            binding.btnCreate.text = text
        }
    }

    fun setCreateLoading(isLoading: Boolean) {

        _binding?.let { binding ->

            binding.btnCreate.isEnabled = !isLoading

            binding.btnCreate.text = when {
                isLoading -> "Please wait..."
                else -> binding.btnCreate.tag?.toString() ?: "Create"
            }
        }
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
