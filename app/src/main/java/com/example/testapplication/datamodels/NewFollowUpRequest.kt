package com.example.testapplication.datamodels

data class NewFollowUpRequest(
    val enquiryId: Int,
    val remarks: String,
    val followUpType: String,
    val date: String,
    val time: String
)
