package com.example.testapplication.datamodels

import com.google.gson.annotations.SerializedName

data class CreateSellingEnquiryRequest(

    @SerializedName("mobile")
    val mobile: String,

    @SerializedName("customer_name")
    val customerName: String,

    @SerializedName("customer_type")
    val customerType: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("district")
    val district: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("pin_code")
    val pinCode: String,

    @SerializedName("whatsapp_no")
    val whatsappNo: String,

    @SerializedName("enquiry_no")
    val enquiryNo: String,

    @SerializedName("enquiry_date")
    val enquiryDate: String,

    @SerializedName("enquiry_source")
    val enquirySource: String,

    @SerializedName("enquiry_sub_source")
    val enquirySubSource: String,

    @SerializedName("maker")
    val maker: String,

    @SerializedName("model")
    val model: String,

    @SerializedName("variant")
    val variant: String,

    @SerializedName("fuel_type")
    val fuelType: String,

    @SerializedName("registration_no")
    val registrationNo: String
)