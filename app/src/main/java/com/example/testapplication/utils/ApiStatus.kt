package com.example.testapplication.utils

class ApiStatus {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    data class Resource<out T>(val status: Status, val data: T?, val message: String?, val isNetworkError: Boolean = false) {
        companion object {

            fun <T> success(data: T?): Resource<T> {
                return Resource(Status.SUCCESS, data, null)
            }

            fun <T> error(msg: String): Resource<T> {
                return Resource(Status.ERROR, null, msg)
            }

            fun <T> loading(): Resource<T> {
                return Resource(Status.LOADING, null, null)
            }

        }
    }
}