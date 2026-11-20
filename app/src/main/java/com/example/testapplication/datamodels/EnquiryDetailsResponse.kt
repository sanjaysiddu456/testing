package com.example.testapplication.datamodels
import java.io.Serializable


data class EnquiryDetailsResponse(
    val status: Boolean,
    val message: String,
    val data: Enquiry
)

data class Enquiry(
    val id: Int = 0,

    // Customer details
    val customerName: String = "",
    val mobileNumber: String = "",
    val customerType: String = "",
    val gender: String = "",
    val email: String = "",
    val state: String = "",
    val district: String = "",
    val city: String = "",
    val address: String = "",
    val pinCode: String = "",
    val whatsappNo: String = "",

    // Existing callback/details fields
    val isCertified: Boolean = false,
    val callBackIn: String = "",
    val callBackDate: String = "",
    val callBackTime: String = "",

    // Enquiry details
    val enquiryNo: String = "",
    val dateOfEnquiry: String = "",
    val timeOfEnquiry: String = "",
    val enquirySource: String = "",
    val enquirySubSource: String = "",
    val regName: String = "",
    val regNo: String = "",
    val maker: String = "",
    val model: String = "",
    val variant: String = "",
    val fuelType: String = "",
    val carName: String = "",

    val isClosed: Boolean = false,
    val closedDateTime: String = ""
) : Serializable