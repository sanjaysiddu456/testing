package com.example.testapplication.datamodels
import java.io.Serializable

data class responseGetEnquiries(val status: Boolean,

                                val message: String,

                                val data: List<Enquiry>)


data class Enquiry(
    val id: Int,
    val customerName: String,
    val mobileNumber: String,
    val email: String = "",
    val isCertified: Boolean = false,
    val regName: String = "",
    val regNo: String = "",
    val maker: String = "",
    val model: String = "",
    val carName: String = "",
    val callBackIn: String = "",
    val callBackDate: String = "",
    val callBackTime: String = ""
) : Serializable