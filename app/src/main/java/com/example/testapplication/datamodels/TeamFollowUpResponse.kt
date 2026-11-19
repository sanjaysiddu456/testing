package com.example.testapplication.datamodels


data class TeamFollowUpResponse(
    val status: Boolean,
    val message: String,
    val data: List<TeamFollowUpItem>
)

data class TeamFollowUpItem(
    val id: Int,
    val consultantName: String,
    val callBacks: Int,
    val showroomVisit: Int
)
