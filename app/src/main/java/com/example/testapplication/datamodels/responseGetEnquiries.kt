package com.example.testapplication.datamodels
import java.io.Serializable

data class responseGetEnquiries(val status: Boolean,

                                val message: String,

                                val data: List<Enquiry>)


