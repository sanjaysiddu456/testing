package com.example.testapplication.datamodels

data class responseGetEnquiries(val status: Boolean,

                                val message: String,

                                val data: List<Enquiry>)

data class Enquiry(

    val id: Int,

    val customerName: String,

    val mobileNumber: String,

    val isCertified: Boolean,

    val carName: String,

    val callBackIn: String,

    val callBackDate: String,

    val callBackTime: String

)
