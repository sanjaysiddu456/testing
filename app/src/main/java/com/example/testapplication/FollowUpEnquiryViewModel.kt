package com.example.testapplication

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testapplication.datamodels.responseGetEnquiries
import com.example.testapplication.datamodels.responseGetFollowUps
import com.example.testapplication.enquiryFeature.EnquiryViewModel
import com.example.testapplication.network.ApiProvider
import com.example.testapplication.network.RepositoryApprik
import com.example.testapplication.utils.ApiStatus
import kotlinx.coroutines.launch

class FollowUpEnquiryViewModel(
    private val repository: RepositoryApprik
) : ViewModel() {

    private val _followUpResponse =
        MutableLiveData<ApiStatus.Resource<responseGetFollowUps>>()

    val followUpResponse: LiveData<ApiStatus.Resource<responseGetFollowUps>>
        get() = _followUpResponse


    fun getFollowUps(
        enquiryId: Int,
        token: String
    ) {

        viewModelScope.launch {

            try {

                _followUpResponse.postValue(
                    ApiStatus.Resource.loading()
                )

                repository.getFollowUps(enquiryId, token).collect { response ->

                    if (response.status) {

                        _followUpResponse.postValue(
                            ApiStatus.Resource.success(response)
                        )

                    } else {

                        _followUpResponse.postValue(
                            ApiStatus.Resource.error(response.message)
                        )
                    }
                }

            } catch (e: Exception) {

                _followUpResponse.postValue(
                    ApiStatus.Resource.error(
                        e.message ?: "Unable to load follow-ups"
                    )
                )

                e.printStackTrace()
            }
        }
    }
}

class FollowUpEnquiryViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(FollowUpEnquiryViewModel::class.java)) {

            val repository = RepositoryApprik(
                apiService = ApiProvider.apiService,
                context = context.applicationContext
            )

            @Suppress("UNCHECKED_CAST")
            return FollowUpEnquiryViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}