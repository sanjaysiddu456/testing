package com.example.testapplication.viewmodels



import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testapplication.datamodels.LeaderboardResponse
import com.example.testapplication.datamodels.ReportsResponse
import com.example.testapplication.network.ApiProvider
import com.example.testapplication.network.RepositoryApprik
import com.example.testapplication.utils.ApiStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class ReportsViewModel(
    private val repository: RepositoryApprik
) : ViewModel() {

    // ==========================================
    // REPORTS RESPONSE
    // ==========================================

    private val _reportsResponse =
        MutableLiveData<ApiStatus.Resource<ReportsResponse>>()

    val reportsResponse: LiveData<ApiStatus.Resource<ReportsResponse>>
        get() = _reportsResponse


    // ==========================================
    // LEADERBOARD RESPONSE
    // ==========================================

    private val _leaderboardResponse =
        MutableLiveData<ApiStatus.Resource<LeaderboardResponse>>()

    val leaderboardResponse: LiveData<ApiStatus.Resource<LeaderboardResponse>>
        get() = _leaderboardResponse


    // ==========================================
    // GET REPORTS
    // ==========================================

    fun getReports() {

        viewModelScope.launch {

            _reportsResponse.value = ApiStatus.Resource.loading()

            try {

                repository.getReports()
                    .catch { exception ->

                        if (exception is CancellationException) {
                            throw exception
                        }

                        _reportsResponse.value =
                            ApiStatus.Resource.error(
                                exception.message ?: "Unable to load reports"
                            )
                    }
                    .collect { response ->

                        _reportsResponse.value =
                            ApiStatus.Resource.success(response)
                    }

            } catch (exception: CancellationException) {

                throw exception

            } catch (exception: Exception) {

                _reportsResponse.value =
                    ApiStatus.Resource.error(
                        exception.message ?: "Something went wrong"
                    )
            }
        }
    }


    // ==========================================
    // GET LEADERBOARD
    // ==========================================

    fun getLeaderboard() {

        viewModelScope.launch {

            _leaderboardResponse.value =
                ApiStatus.Resource.loading()

            try {

                repository.getLeaderboard()
                    .catch { exception ->

                        if (exception is CancellationException) {
                            throw exception
                        }

                        _leaderboardResponse.value =
                            ApiStatus.Resource.error(
                                exception.message
                                    ?: "Unable to load leaderboard"
                            )
                    }
                    .collect { response ->

                        _leaderboardResponse.value =
                            ApiStatus.Resource.success(response)
                    }

            } catch (exception: CancellationException) {

                throw exception

            } catch (exception: Exception) {

                _leaderboardResponse.value =
                    ApiStatus.Resource.error(
                        exception.message ?: "Unable to load leaderboard"
                    )
            }
        }
    }
}



class ReportsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {

            val repository = RepositoryApprik(
                apiService = ApiProvider.apiService,
                context = context.applicationContext
            )

            @Suppress("UNCHECKED_CAST")
            return ReportsViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}

