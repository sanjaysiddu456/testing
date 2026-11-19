
package com.example.testapplication.datamodels

data class ReportsResponse(
    val status: Boolean,
    val message: String,
    val data: ReportsData
)

data class ReportsData(
    val enquiredCount: Int,
    val invoiceConfirmedCount: Int
)
