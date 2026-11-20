package com.example.testapplication.datamodels

data class UpdateSellingEnquiryRequest(
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
    val whatsappNo: String,
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