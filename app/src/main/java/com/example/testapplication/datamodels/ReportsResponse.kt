
package com.example.testapplication.datamodels

data class ReportsResponse(
    val status: Boolean,
    val message: String,
    val data: ReportsData
)

data class ReportsData(
    val enquiredCount: Int,
    val invoiceConfirmedCount: Int,
    val leaderboard: List<LeaderboardItem>
)

data class LeaderboardItem(
    val id: Int,
    val consultantName: String,
    val followUps: Int,
    val invoiceConfirmed: Int
)
