package com.example.testapplication.network

import android.content.Context
import com.example.testapplication.datamodels.CreateFollowUpResponse
import com.example.testapplication.datamodels.CreateSellingEnquiryRequest
import com.example.testapplication.datamodels.CreateSellingEnquiryResponse
import com.example.testapplication.datamodels.DashboardResponse
import com.example.testapplication.datamodels.EnquiryDetailsResponse
import com.example.testapplication.datamodels.FollowUpResponse
import com.example.testapplication.datamodels.LeaderboardResponse
import com.example.testapplication.datamodels.NewFollowUpRequest
import com.example.testapplication.datamodels.ReportsResponse
import com.example.testapplication.datamodels.StockResponse
import com.example.testapplication.datamodels.TeamFollowUpResponse
import com.example.testapplication.datamodels.responseGetEnquiries
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


    suspend fun getEnquiryDetails(
        enquiryId: Int,
        token: String
    ): Flow<EnquiryDetailsResponse> = flow {

        // =====================================
        // DUMMY JSON RESPONSE
        // =====================================

        val json = context.assets
            .open("enquiry_details.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            EnquiryDetailsResponse::class.java
        )

        emit(response)

        // =====================================
        // ACTUAL API - ENABLE WHEN READY
        // =====================================

        /*
        val apiResponse = ApiProvider.apiService.getEnquiryDetails(
            enquiryId = enquiryId,
            token = "Bearer $token"
        )

        if (apiResponse.isSuccessful && apiResponse.body() != null) {
            emit(apiResponse.body()!!)
        } else {
            throw Exception(
                apiResponse.errorBody()?.string()
                    ?: apiResponse.message()
            )
        }
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

        val json = context.assets.open("dashboard.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            DashboardResponse::class.java
        )

        emit(response)

        // Actual API - enable when backend is ready

        /*
        val response = apiService.getDashboard()

        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Exception(response.message())
        }
        */

    }.flowOn(Dispatchers.IO)


    // ==========================================
    // TEAM FOLLOW-UPS API
    // ==========================================

    suspend fun getTeamFollowUps(): Flow<TeamFollowUpResponse> = flow {

        val json = context.assets.open("team_followups.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            TeamFollowUpResponse::class.java
        )

        emit(response)

        // Actual API - enable when backend is ready

        /*
        val response = apiService.getTeamFollowUps()

        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Exception(response.message())
        }
        */

    }.flowOn(Dispatchers.IO)


    suspend fun getReports(): Flow<ReportsResponse> = flow {

        val json = context.assets.open("reports.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            ReportsResponse::class.java
        )

        emit(response)

        // Actual API - enable when backend is ready

        /*
        val response = apiService.getReports()

        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Exception(response.message())
        }
        */

    }.flowOn(Dispatchers.IO)


    // ==========================================
    // LEADERBOARD API
    // ==========================================

    suspend fun getLeaderboard(): Flow<LeaderboardResponse> = flow {

        val json = context.assets.open("leaderboard.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            LeaderboardResponse::class.java
        )

        emit(response)

        // Actual API - enable when backend is ready

        /*
        val response = apiService.getLeaderboard()

        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Exception(response.message())
        }
        */

    }.flowOn(Dispatchers.IO)// ==========================================
    // LEADERBOARD API
    // ==========================================

    suspend fun getFollowUps( enquiryId: Int,
                              token: String): Flow<FollowUpResponse> = flow {

        val json = context.assets.open("followups.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(
            json,
            FollowUpResponse::class.java
        )

        emit(response)

        // Actual API - enable when backend is ready

        /*
        val response = apiService.getLeaderboard()

        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Exception(response.message())
        }
        */

    }.flowOn(Dispatchers.IO)


    fun createSellingEnquiry(
        token: String,
        request: CreateSellingEnquiryRequest
    ): Flow<CreateSellingEnquiryResponse> = flow {

        val response = apiService.createSellingEnquiry(
            token = "Bearer $token",
            request = request
        )

        emit(response)

    }.flowOn(Dispatchers.IO)

}