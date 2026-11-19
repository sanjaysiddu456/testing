package com.example.testapplication.enquiryFeature


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testapplication.datamodels.EnquiryDetailsResponse
import com.example.testapplication.datamodels.responseGetEnquiries
import com.example.testapplication.network.ApiProvider
import com.example.testapplication.network.RepositoryApprik
import com.example.testapplication.utils.ApiStatus
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class EnquiryViewModel(
    private val repository: RepositoryApprik
) : ViewModel() {


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

                        if (exception is CancellationException) {
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

            } catch (exception: CancellationException) {

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

    private val _enquiryResponse =
        MutableLiveData<ApiStatus.Resource<responseGetEnquiries>>()

    val enquiryResponse: LiveData<ApiStatus.Resource<responseGetEnquiries>>
        get() = _enquiryResponse

    fun getEnquiries(token: String) {

        viewModelScope.launch {

            try {

                _enquiryResponse.postValue(
                    ApiStatus.Resource.loading()
                )

                repository.getEnquries(token).collect { response ->

                    if (response.status) {

                        _enquiryResponse.postValue(
                            ApiStatus.Resource.success(response)
                        )

                    } else {

                        _enquiryResponse.postValue(
                            ApiStatus.Resource.error(response.message)
                        )
                    }
                }

            } catch (e: Exception) {

                _enquiryResponse.postValue(
                    ApiStatus.Resource.error(
                        e.message ?: "Something went wrong"
                    )
                )

                e.printStackTrace()
            }
        }

    }


}

class EnquiryViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(EnquiryViewModel::class.java)) {

            val repository = RepositoryApprik(
                apiService = ApiProvider.apiService,
                context = context.applicationContext
            )

            @Suppress("UNCHECKED_CAST")
            return EnquiryViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}