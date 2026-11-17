package com.example.testapplication.network

import com.example.testapplication.datamodels.CreateFollowUpResponse
import com.example.testapplication.datamodels.NewFollowUpRequest
import com.example.testapplication.datamodels.StockResponse
import com.example.testapplication.datamodels.responseGetEnquiries
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {


    @GET("enquiries")
    suspend fun getEnquiries(): Response<responseGetEnquiries>

    @POST("enquiries/follow-ups") // Replace with actual endpoint
    suspend fun createFollowUp(
        @Body request: NewFollowUpRequest
    ): Response<CreateFollowUpResponse>

    @GET("stocks")
    suspend fun getStocks(): Response<StockResponse>

}