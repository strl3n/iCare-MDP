package com.istts.finalproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istts.finalproject.data.local.entity.UserEntity
import com.istts.finalproject.data.remote.repository.AuthRepository
import com.istts.finalproject.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authResult = MutableLiveData<Resource<Pair<UserEntity, String>>>()
    val authResult: LiveData<Resource<Pair<UserEntity, String>>> = _authResult

    private val _registerResult = MutableLiveData<Resource<UserEntity>>()
    val registerResult: LiveData<Resource<UserEntity>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = Resource.Loading()
            val result = authRepository.register(name, email, password)
            _registerResult.value = result
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = Resource.Loading()
            val result = authRepository.login(email, password)
            _authResult.value = result
        }
    }

    fun googleLogin(email: String, name: String, profilePicture: String?) {
        viewModelScope.launch {
            _authResult.value = Resource.Loading()
            val result = authRepository.googleLogin(email, name, profilePicture)
            _authResult.value = result
        }
    }

    fun getCurrentUser(userId: Int): LiveData<Resource<UserEntity>> {
        val result = MutableLiveData<Resource<UserEntity>>()
        viewModelScope.launch {
            result.value = authRepository.getCurrentUser(userId)
        }
        return result
    }
}