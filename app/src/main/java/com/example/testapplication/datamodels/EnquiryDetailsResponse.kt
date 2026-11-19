package com.example.testapplication.datamodels
import java.io.Serializable


data class EnquiryDetailsResponse(
    val status: Boolean,
    val message: String,
    val data: Enquiry
)

data class Enquiry(
    val id: Int,
    val customerName: String,
    val isCertified: Boolean,
    val mobileNumber: String,
    val email: String,
    val callBackIn: String,
    val callBackDate: String,
    val callBackTime: String,
    val enquiryNo: String,
    val dateOfEnquiry: String,
    val timeOfEnquiry: String,
    val enquirySource: String,
    val regName: String,
    val regNo: String,
    val maker: String,
    val model: String,
    val carName: String,
    val isClosed: Boolean,
    val closedDateTime: String
) : java.io.Serializable