package com.example.testapplication.datamodels

data class StockResponse(
    val status: Boolean,
    val message: String,
    val data: List<Stock>
)

data class Stock(
    val id: Int,
    val carName: String,
    val stockNumber: String,
    val availability: String,
    val photosUploaded: Boolean,
    val photoCount: Int,
    val images: List<String>,
    val stockDate: String,
    val enquiries: List<StockEnquiry>
)

data class StockEnquiry(
    val id: Int,
    val customerName: String,
    val mobileNumber: String,
    val isCertified: Boolean,
    val lastEnquiredOn: String
)