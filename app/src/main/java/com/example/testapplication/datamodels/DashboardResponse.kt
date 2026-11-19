package com.example.testapplication.datamodels

data class DashboardResponse(
    val status: Boolean,
    val message: String,
    val data: DashboardData
)

data class DashboardData(
    val consultantName: String,
    val totalFollowUps: Int,
    val callBackCount: Int,
    val dealerVisitCount: Int
)