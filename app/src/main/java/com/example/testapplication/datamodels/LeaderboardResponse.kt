package com.example.testapplication.datamodels


data class LeaderboardResponse(
    val status: Boolean,
    val message: String,
    val data: List<LeaderboardItem>
)

data class LeaderboardItem(
    val id: Int,
    val consultantName: String,
    val followUps: Int,
    val invoiceConfirmed: Int
)