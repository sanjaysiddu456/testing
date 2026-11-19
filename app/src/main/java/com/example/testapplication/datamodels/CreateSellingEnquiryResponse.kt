package com.example.testapplication.datamodels


data class CreateSellingEnquiryResponse(
    val status: Boolean,
    val message: String,
    val data: Any? = null
)