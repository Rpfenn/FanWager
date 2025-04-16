package edu.quinnipiac.ser210.finalproject.api

sealed class NetworkResponse<out T> {
    data class Success<out T>(val data : T) : NetworkResponse<T>()
    data class Error(val message : String) : NetworkResponse<Nothing>()
    object Loading : NetworkResponse<Nothing>()
}