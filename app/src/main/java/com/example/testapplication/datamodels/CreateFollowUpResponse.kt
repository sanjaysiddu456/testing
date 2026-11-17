package com.example.testapplication.datamodels

data class CreateFollowUpResponse(
    val status: Boolean,
    val message: String,
    val data: CreateFollowUpData? = null
)

data class CreateFollowUpData(
    val id: Int? = null
)