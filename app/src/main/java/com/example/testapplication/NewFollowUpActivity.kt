package com.example.testapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.testapplication.databinding.ActivityNewFollowUpBinding
import com.example.testapplication.datamodels.NewFollowUpRequest
import com.example.testapplication.utils.ApiStatus
import java.util.Calendar
import java.util.Locale

class NewFollowUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewFollowUpBinding

    private val viewModel: FollowUpEnquiryViewModel by viewModels {
        FollowUpEnquiryViewModelFactory(
            applicationContext
        )
    }

    private var enquiryId: Int = -1

    private var selectedDate = ""
    private var selectedTime = ""

    // Change to true when backend API is ready.
    private val ENABLE_CREATE_FOLLOWUP_API = false

    private val followUpTypes = listOf(
        "Dealer Visit",
        "Call Back"
    )

    companion object {
        const val EXTRA_ENQUIRY_ID = "extra_enquiry_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewFollowUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enquiryId = intent.getIntExtra(EXTRA_ENQUIRY_ID, -1)

        if (enquiryId == -1) {
            Toast.makeText(
                this,
                "Enquiry ID not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setupFollowUpDropdown()
        setupDatePicker()
        setupTimePicker()
        setupClickListeners()
        observeCreateFollowUpResponse()
    }

    // ==========================================
    // FOLLOW-UP TYPE
    // ==========================================

    private fun setupFollowUpDropdown() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            followUpTypes
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerFollowUpType.adapter = adapter
    }

    // ==========================================
    // DATE PICKER
    // ==========================================

    private fun setupDatePicker() {

        binding.layoutDatePicker.setOnClickListener {

            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->

                    selectedDate = String.format(
                        Locale.getDefault(),
                        "%02d/%02d/%04d",
                        day,
                        month + 1,
                        year
                    )

                    binding.tvSelectedDate.text = selectedDate

                    binding.tvSelectedDate.setTextColor(
                        getColor(android.R.color.black)
                    )
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.datePicker.minDate =
                System.currentTimeMillis() - 1000

            datePicker.show()
        }
    }

    // ==========================================
    // TIME PICKER
    // ==========================================

    private fun setupTimePicker() {

        binding.layoutTimePicker.setOnClickListener {

            val calendar = Calendar.getInstance()

            val timePicker = TimePickerDialog(
                this,
                { _, hour, minute ->

                    selectedTime = formatTime(hour, minute)

                    binding.tvSelectedTime.text = selectedTime

                    binding.tvSelectedTime.setTextColor(
                        getColor(android.R.color.black)
                    )
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )

            timePicker.show()
        }
    }

    private fun formatTime(
        hour: Int,
        minute: Int
    ): String {

        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        val period = if (hour < 12) "AM" else "PM"

        return String.format(
            Locale.getDefault(),
            "%02d:%02d %s",
            displayHour,
            minute,
            period
        )
    }

    // ==========================================
    // CLICK LISTENERS
    // ==========================================

    private fun setupClickListeners() {

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUpdateFollowUp.setOnClickListener {
            validateAndUpdate()
        }
    }

    // ==========================================
    // VALIDATION
    // ==========================================

    private fun validateAndUpdate() {

        val remarks = binding.etRemarks.text.toString().trim()

        if (remarks.isEmpty()) {
            binding.etRemarks.error = "Please enter remarks"
            binding.etRemarks.requestFocus()
            return
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(
                this,
                "Please select date",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(
                this,
                "Please select time",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val request = NewFollowUpRequest(
            enquiryId = enquiryId,
            remarks = remarks,
            followUpType = binding.spinnerFollowUpType.selectedItem.toString(),
            date = selectedDate,
            time = selectedTime
        )

        updateFollowUp(request)
    }

    // ==========================================
    // UPDATE FOLLOW-UP
    // ==========================================

    private fun updateFollowUp(request: NewFollowUpRequest) {

        if (ENABLE_CREATE_FOLLOWUP_API) {

            // Actual API call
            viewModel.createFollowUp(request)

        } else {

            // Temporary dummy behavior until backend is ready.
            Toast.makeText(
                this,
                "Follow-up details validated successfully",
                Toast.LENGTH_SHORT
            ).show()

            // Do not send data back to the previous Activity.
            // Do not finish this Activity.
        }
    }

    // ==========================================
    // OBSERVE API RESPONSE
    // ==========================================

    private fun observeCreateFollowUpResponse() {

        viewModel.createFollowUpResponse.observe(this) { resource ->

            when (resource.status) {

                ApiStatus.Status.LOADING -> {

                    binding.btnUpdateFollowUp.isEnabled = false
                    binding.btnUpdateFollowUp.text = "Updating..."
                }

                ApiStatus.Status.SUCCESS -> {

                    binding.btnUpdateFollowUp.isEnabled = true
                    binding.btnUpdateFollowUp.text = "Update"

                    Toast.makeText(
                        this,
                        resource.data?.message
                            ?: "Follow-up created successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Close this screen after successful API response.
                    // No result is returned to the previous Activity.

                    finish()
                }

                ApiStatus.Status.ERROR -> {

                    binding.btnUpdateFollowUp.isEnabled = true
                    binding.btnUpdateFollowUp.text = "Update"

                    Toast.makeText(
                        this,
                        resource.message ?: "Unable to create follow-up",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}