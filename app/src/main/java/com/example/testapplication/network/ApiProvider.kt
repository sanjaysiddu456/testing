package com.example.testapplication.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {

    val apiService: ApiService by lazy {

        val baseurl=""


        Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}