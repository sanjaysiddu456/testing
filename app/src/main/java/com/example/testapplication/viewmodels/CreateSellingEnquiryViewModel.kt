package com.example.testapplication.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testapplication.datamodels.CreateSellingEnquiryRequest
import com.example.testapplication.datamodels.CreateSellingEnquiryResponse
import com.example.testapplication.datamodels.EnquiryDetailsResponse
import com.example.testapplication.datamodels.UpdateSellingEnquiryRequest
import com.example.testapplication.network.ApiProvider
import com.example.testapplication.network.RepositoryApprik
import com.example.testapplication.utils.ApiStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
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


    private val _enquiryDetailsResponse =
        MutableLiveData<ApiStatus.Resource<EnquiryDetailsResponse>>()

    val enquiryDetailsResponse: LiveData<ApiStatus.Resource<EnquiryDetailsResponse>>
        get() = _enquiryDetailsResponse

    fun getEnquiryDetails(
        enquiryId: Int,
        token: String
    ) {

        viewModelScope.launch {

            _enquiryDetailsResponse.value =
                ApiStatus.Resource.loading()

            try {

                repository.getEnquiryDetails(
                    enquiryId,
                    token
                )
                    .catch { exception ->

                        if (exception is kotlin.coroutines.cancellation.CancellationException) {
                            throw exception
                        }

                        _enquiryDetailsResponse.value =
                            ApiStatus.Resource.error(
                                exception.message
                                    ?: "Unable to fetch enquiry details"
                            )
                    }
                    .collect { response ->

                        if (response.status) {

                            _enquiryDetailsResponse.value =
                                ApiStatus.Resource.success(response)

                        } else {

                            _enquiryDetailsResponse.value =
                                ApiStatus.Resource.error(response.message)
                        }
                    }

            } catch (exception: kotlin.coroutines.cancellation.CancellationException) {

                throw exception

            } catch (exception: Exception) {

                _enquiryDetailsResponse.value =
                    ApiStatus.Resource.error(
                        exception.message
                            ?: "Something went wrong"
                    )
            }
        }
    }

    private val _updateEnquiryResponse =
        MutableLiveData<ApiStatus.Resource<EnquiryDetailsResponse>>()

    val updateEnquiryResponse: LiveData<ApiStatus.Resource<EnquiryDetailsResponse>>
        get() = _updateEnquiryResponse

    fun updateSellingEnquiry(
        enquiryId: Int,
        token: String,
        request: UpdateSellingEnquiryRequest
    ) {
        viewModelScope.launch {
            _updateEnquiryResponse.value = ApiStatus.Resource.loading()

            try {
                repository.updateSellingEnquiry(
                    enquiryId,
                    token,
                    request
                )
                    .catch { exception ->
                        if (exception is kotlin.coroutines.cancellation.CancellationException) throw exception

                        _updateEnquiryResponse.value =
                            ApiStatus.Resource.error(
                                exception.message ?: "Unable to update enquiry"
                            )
                    }
                    .collect { response ->
                        if (response.status) {
                            _updateEnquiryResponse.value =
                                ApiStatus.Resource.success(response)
                        } else {
                            _updateEnquiryResponse.value =
                                ApiStatus.Resource.error(response.message)
                        }
                    }

            } catch (exception: kotlin.coroutines.cancellation.CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _updateEnquiryResponse.value =
                    ApiStatus.Resource.error(
                        exception.message ?: "Something went wrong"
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