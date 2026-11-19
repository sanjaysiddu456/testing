package com.example.testapplication.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testapplication.datamodels.CreateSellingEnquiryRequest
import com.example.testapplication.datamodels.CreateSellingEnquiryResponse
import com.example.testapplication.network.ApiProvider
import com.example.testapplication.network.RepositoryApprik
import com.example.testapplication.utils.ApiStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CreateSellingEnquiryViewModel(
    private val repository: RepositoryApprik
) : ViewModel() {

    private val _createSellingEnquiryResponse =
        MutableLiveData<ApiStatus.Resource<CreateSellingEnquiryResponse>>()

    val createSellingEnquiryResponse:
            LiveData<ApiStatus.Resource<CreateSellingEnquiryResponse>>
        get() = _createSellingEnquiryResponse

    fun createSellingEnquiry(
        token: String,
        request: CreateSellingEnquiryRequest
    ) {

        viewModelScope.launch {

            _createSellingEnquiryResponse.value =
                ApiStatus.Resource.loading()

            try {

                repository.createSellingEnquiry(
                    token,
                    request
                ).collect { response ->

                    if (response.status) {

                        _createSellingEnquiryResponse.postValue(
                            ApiStatus.Resource.success(response)
                        )

                    } else {

                        _createSellingEnquiryResponse.postValue(
                            ApiStatus.Resource.error(response.message)
                        )
                    }
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                _createSellingEnquiryResponse.postValue(
                    ApiStatus.Resource.error(
                        e.message ?: "Unable to create selling enquiry"
                    )
                )
            }
        }
    }
}
class CreateSellingEnquiryViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                CreateSellingEnquiryViewModel::class.java
            )
        ) {

            val repository = RepositoryApprik(
                apiService = ApiProvider.apiService,
                context = context.applicationContext
            )

            return CreateSellingEnquiryViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}