package com.example.testapplication.network

import android.content.Context
import com.example.testapplication.datamodels.responseGetEnquiries
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RepositoryApprik(
    private val apiService: ApiService,
    private val context: Context
) {
    constructor(apiService: Any, context: Context) : this(apiService, context)

    suspend fun getEnquries(
        token: String
    ): Flow<responseGetEnquiries> = flow {

        // --------------------------------
        // DUMMY JSON RESPONSE
        // --------------------------------

        val json = context.assets.open("enquiries.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            responseGetEnquiries::class.java
        )

        emit(response)


        // --------------------------------
        // ACTUAL API RESPONSE
        // Uncomment this when backend API is ready
        // --------------------------------

        /*
        val response = apiService.getEnquiries()

        emit(response)
        */

    }.flowOn(Dispatchers.IO)
}