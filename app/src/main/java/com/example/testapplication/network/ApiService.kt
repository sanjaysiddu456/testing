package com.example.testapplication.network

import com.example.testapplication.datamodels.CreateFollowUpResponse
import com.example.testapplication.datamodels.DashboardResponse
import com.example.testapplication.datamodels.EnquiryDetailsResponse
import com.example.testapplication.datamodels.FollowUpResponse
import com.example.testapplication.datamodels.LeaderboardResponse
import com.example.testapplication.datamodels.NewFollowUpRequest
import com.example.testapplication.datamodels.ReportsResponse
import com.example.testapplication.datamodels.StockResponse
import com.example.testapplication.datamodels.TeamFollowUpResponse
import com.example.testapplication.datamodels.responseGetEnquiries
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {


    @GET("enquiries")
    suspend fun getEnquiries(): Response<responseGetEnquiries>

    @POST("enquiries/follow-ups") // Replace with actual endpoint
    suspend fun createFollowUp(
        @Body request: NewFollowUpRequest
    ): Response<CreateFollowUpResponse>

    @GET("stocks")
    suspend fun getStocks(): Response<StockResponse>

    @GET("reports")
    suspend fun getReports(): retrofit2.Response<ReportsResponse>

    @GET("leaderboard")
    suspend fun getLeaderboard(): retrofit2.Response<LeaderboardResponse>

    @GET("dashboard")
    suspend fun getDashboard(): retrofit2.Response<DashboardResponse>

    @GET("team-followups")
    suspend fun getTeamFollowUps(): retrofit2.Response<TeamFollowUpResponse>

    @GET("enquiries/{enquiryId}")
    suspend fun getEnquiryDetails(
        @Path("enquiryId") enquiryId: Int,
        @Header("Authorization") token: String
    ): retrofit2.Response<EnquiryDetailsResponse>


    @GET("enquiries/{enquiryId}/followups")
    suspend fun getFollowUps(
        @Path("enquiryId") enquiryId: Int,
        @Header("Authorization") token: String
    ): retrofit2.Response<FollowUpResponse>
}