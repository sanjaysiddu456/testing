package com.example.testapplication.network

import android.content.Context
import com.example.testapplication.datamodels.responseGetEnquiries
import com.example.testapplication.datamodels.responseGetFollowUps
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RepositoryApprik(
    private val apiService: ApiService,
    private val context: Context
) {

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




    suspend fun getFollowUps(
        enquiryId: Int,
        token: String
    ): Flow<responseGetFollowUps> = flow {

        // =================================
        // DUMMY JSON RESPONSE
        // =================================

        val json = context.assets.open("followups.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            responseGetFollowUps::class.java
        )

        // Filter follow-ups for the selected enquiry
        val filteredResponse = response.copy(
            data = response.data
                .filter { it.enquiryId == enquiryId }
                .sortedByDescending { it.id }
        )

        emit(filteredResponse)


        // =================================
        // ACTUAL API RESPONSE
        // Uncomment when backend is ready
        // =================================

        /*
        val response = apiService.getFollowUps(
            token = token,
            enquiryId = enquiryId
        )

        emit(response)
        */

    }.flowOn(Dispatchers.IO)

}