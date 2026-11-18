package com.example.testapplication.network

import android.content.Context
import com.example.testapplication.datamodels.CreateFollowUpResponse
import com.example.testapplication.datamodels.DashboardResponse
import com.example.testapplication.datamodels.NewFollowUpRequest
import com.example.testapplication.datamodels.ReportsResponse
import com.example.testapplication.datamodels.StockResponse
import com.example.testapplication.datamodels.responseGetEnquiries
import com.example.testapplication.datamodels.responseGetFollowUps
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

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

    suspend fun createFollowUp(
        request: NewFollowUpRequest
    ): Response<CreateFollowUpResponse> {

        return apiService.createFollowUp(request)
    }


    suspend fun getStocks(): Flow<StockResponse> = flow {

        // =====================================
        // DUMMY JSON RESPONSE
        // =====================================

        val json = context.assets.open("stocks.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            StockResponse::class.java
        )

        emit(response)


        // =====================================
        // ACTUAL API RESPONSE
        // Uncomment when backend is ready
        // =====================================

        /*
        val apiResponse = apiService.getStocks()

        if (apiResponse.isSuccessful) {

            apiResponse.body()?.let { response ->
                emit(response)
            } ?: throw Exception("Empty response from server")

        } else {

            throw Exception(
                apiResponse.errorBody()?.string()
                    ?: "Unable to fetch stocks"
            )
        }
        */

    }.flowOn(Dispatchers.IO)


    suspend fun getDashboard(): Flow<DashboardResponse> = flow {

        // =====================================
        // DUMMY JSON RESPONSE
        // =====================================

        val json = context.assets.open("dashboard.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            DashboardResponse::class.java
        )

        emit(response)

        // =====================================
        // ACTUAL API CALL - UNCOMMENT LATER
        // =====================================

        /*
        val response = apiService.getDashboard()

        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Exception(response.message())
        }
        */

    }.flowOn(Dispatchers.IO)


    suspend fun getReports(): Flow<ReportsResponse> = flow {

        // =====================================
        // DUMMY JSON RESPONSE
        // =====================================

        val json = context.assets.open("reports.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            ReportsResponse::class.java
        )

        emit(response)

        // =====================================
        // ACTUAL API CALL - ENABLE LATER
        // =====================================

        /*
        val response = apiService.getReports()

        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Exception(response.message())
        }
        */

    }.flowOn(Dispatchers.IO)

}