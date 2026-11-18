package com.example.testapplication.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testapplication.datamodels.StockResponse
import com.example.testapplication.network.ApiProvider
import com.example.testapplication.network.RepositoryApprik
import com.example.testapplication.utils.ApiStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class StockViewModel(
    private val repository: RepositoryApprik
) : ViewModel() {

    private val _stockResponse =
        MutableLiveData<ApiStatus.Resource<StockResponse>>()

    val stockResponse: LiveData<ApiStatus.Resource<StockResponse>>
        get() = _stockResponse

    fun getStocks() {

        viewModelScope.launch {

            try {

                _stockResponse.value =
                    ApiStatus.Resource.loading()

                repository.getStocks().collect { response ->

                    if (response.status) {

                        _stockResponse.postValue(
                            ApiStatus.Resource.success(response)
                        )

                    } else {

                        _stockResponse.postValue(
                            ApiStatus.Resource.error(
                                response.message
                            )
                        )
                    }
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _stockResponse.postValue(
                    ApiStatus.Resource.error(
                        e.message ?: "Unable to fetch stocks"
                    )
                )

                e.printStackTrace()
            }
        }
    }
}


// ==========================================
// VIEWMODEL FACTORY
// ==========================================

class StockViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(StockViewModel::class.java)) {

            val repository = RepositoryApprik(
                apiService = ApiProvider.apiService,
                context = context.applicationContext
            )

            @Suppress("UNCHECKED_CAST")
            return StockViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}