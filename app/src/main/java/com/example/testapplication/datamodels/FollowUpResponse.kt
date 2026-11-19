package com.example.testapplication.datamodels

import java.io.Serializable

data class FollowUpResponse(
    val status: Boolean,
    val message: String,
    val data: List<FollowUp>
)

data class FollowUp(
    val id: Int,
    val enquiryId: Int,
    val createdDate: String,
    val createdTime: String,
    val followUpType: String,
    val remarks: String,
    val nextFollowUpType: String,
    val nextFollowUpDate: String,
    val nextFollowUpTime: String
) : Serializable