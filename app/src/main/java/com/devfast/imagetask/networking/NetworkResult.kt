package com.devfast.imagetask.networking

import okhttp3.Headers

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val e: Exception? = null,
    val errorCode: Int? = null,
    val headers: Headers? = null,
) {
    class Success<T>(data: T, headers: Headers? = null) : NetworkResult<T>(data, headers = headers)
    class Error<T>(message: String, e: Exception? = null, errorCode: Int? = 0) : NetworkResult<T>(message = message, e = e, errorCode = errorCode)
    class Loading<T> : NetworkResult<T>()
    class NetworkError<T>(e: Exception? = null) : NetworkResult<T>(e = e)
    class ServerError<T>(e: Exception? = null) : NetworkResult<T>(e = e)
    class TimeOutError<T>(e: Exception? = null) : NetworkResult<T>(e = e)
    class UserExceptionError<T>(e: Exception? = null) : NetworkResult<T>(e = e)
}
