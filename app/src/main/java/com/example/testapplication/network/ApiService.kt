package com.example.testapplication.network

import com.example.testapplication.datamodels.responseGetEnquiries
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {


    @GET("enquiries")
    suspend fun getEnquiries(): Response<responseGetEnquiries>

}