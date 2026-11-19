package com.example.testapplication.viewmodels



import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.testapplication.datamodels.DashboardResponse
import com.example.testapplication.datamodels.TeamFollowUpResponse
import com.example.testapplication.network.ApiProvider
import com.example.testapplication.network.RepositoryApprik
import com.example.testapplication.utils.ApiStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: RepositoryApprik
) : ViewModel() {

    // ==========================================
    // DASHBOARD RESPONSE
    // ==========================================

    private val _dashboardResponse =
        MutableLiveData<ApiStatus.Resource<DashboardResponse>>()

    val dashboardResponse: LiveData<ApiStatus.Resource<DashboardResponse>>
        get() = _dashboardResponse


    // ==========================================
    // TEAM FOLLOW-UPS RESPONSE
    // ==========================================

    private val _teamFollowUpResponse =
        MutableLiveData<ApiStatus.Resource<TeamFollowUpResponse>>()

    val teamFollowUpResponse: LiveData<ApiStatus.Resource<TeamFollowUpResponse>>
        get() = _teamFollowUpResponse


    // ==========================================
    // GET DASHBOARD DETAILS
    // ==========================================

    fun getDashboard() {

        viewModelScope.launch {

            _dashboardResponse.value =
                ApiStatus.Resource.loading()

            try {

                repository.getDashboard()
                    .catch { exception ->

                        if (exception is CancellationException) {
                            throw exception
                        }

                        _dashboardResponse.value =
                            ApiStatus.Resource.error(
                                exception.message ?: "Unable to load dashboard"
                            )
                    }
                    .collect { response ->

                        _dashboardResponse.value =
                            ApiStatus.Resource.success(response)
                    }

            } catch (exception: CancellationException) {

                throw exception

            } catch (exception: Exception) {

                _dashboardResponse.value =
                    ApiStatus.Resource.error(
                        exception.message ?: "Something went wrong"
                    )
            }
        }
    }


    // ==========================================
    // GET TEAM FOLLOW-UPS
    // ==========================================

    fun getTeamFollowUps() {

        viewModelScope.launch {

            _teamFollowUpResponse.value =
                ApiStatus.Resource.loading()

            try {

                repository.getTeamFollowUps()
                    .catch { exception ->

                        if (exception is CancellationException) {
                            throw exception
                        }

                        _teamFollowUpResponse.value =
                            ApiStatus.Resource.error(
                                exception.message
                                    ?: "Unable to load team follow-ups"
                            )
                    }
                    .collect { response ->

                        _teamFollowUpResponse.value =
                            ApiStatus.Resource.success(response)
                    }

            } catch (exception: CancellationException) {

                throw exception

            } catch (exception: Exception) {

                _teamFollowUpResponse.value =
                    ApiStatus.Resource.error(
                        exception.message
                            ?: "Unable to load team follow-ups"
                    )
            }
        }
    }

}


class DashboardViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {

            val repository = RepositoryApprik(
                apiService = ApiProvider.apiService,
                context = context.applicationContext
            )
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}

