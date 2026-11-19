package com.example.testapplication.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testapplication.datamodels.CreateFollowUpResponse
import com.example.testapplication.datamodels.FollowUpResponse
import com.example.testapplication.datamodels.NewFollowUpRequest
import com.example.testapplication.network.ApiProvider
import com.example.testapplication.network.RepositoryApprik
import com.example.testapplication.utils.ApiStatus
import kotlinx.coroutines.launch

class FollowUpEnquiryViewModel(
    private val repository: RepositoryApprik
) : ViewModel() {

    private val _followUpResponse =
        MutableLiveData<ApiStatus.Resource<FollowUpResponse>>()

    val followUpResponse: LiveData<ApiStatus.Resource<FollowUpResponse>>
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


    private val _createFollowUpResponse =
        MutableLiveData<ApiStatus.Resource<CreateFollowUpResponse>>()

    val createFollowUpResponse: LiveData<ApiStatus.Resource<CreateFollowUpResponse>>
        get() = _createFollowUpResponse

    fun createFollowUp(request: NewFollowUpRequest) {

        viewModelScope.launch {

            try {

                _createFollowUpResponse.value =
                    ApiStatus.Resource.loading()

                val response = repository.createFollowUp(request)

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body != null && body.status) {

                        _createFollowUpResponse.value =
                            ApiStatus.Resource.success(body)

                    } else {

                        _createFollowUpResponse.value =
                            ApiStatus.Resource.error(
                                body?.message ?: "Unable to create follow-up"
                            )
                    }

                } else {

                    _createFollowUpResponse.value =
                        ApiStatus.Resource.error(
                            response.errorBody()?.string()
                                ?: "Unable to create follow-up"
                        )
                }

            } catch (e: Exception) {

                _createFollowUpResponse.value =
                    ApiStatus.Resource.error(
                        e.message ?: "Something went wrong"
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