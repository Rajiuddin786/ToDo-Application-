package com.example.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.models.User
import com.example.todoapp.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _signUpState = MutableLiveData<AuthState>()
    val signUpState: LiveData<AuthState> = _signUpState

    private val _loginState = MutableLiveData<AuthState>()
    val loginState: LiveData<AuthState> = _loginState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    fun signUp(name: String, email: String, password: String) {
        if (!validateSignUp(name, email, password)) return

        _signUpState.value = AuthState.Loading
        viewModelScope.launch {
            userRepository.signUp(name.trim(), email.trim(), password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _signUpState.value = AuthState.Success(user)
                }
                .onFailure { error ->
                    _signUpState.value = AuthState.Error(error.message ?: "Sign up failed")
                }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("All fields are required")
            return
        }

        _loginState.value = AuthState.Loading
        viewModelScope.launch {
            userRepository.login(email.trim(), password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _loginState.value = AuthState.Success(user)
                }
                .onFailure { error ->
                    _loginState.value = AuthState.Error(error.message ?: "Login failed")
                }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    private fun validateSignUp(name: String, email: String, password: String): Boolean {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _signUpState.value = AuthState.Error("All fields are required")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _signUpState.value = AuthState.Error("Enter a valid email address")
            return false
        }
        if (password.length < 6) {
            _signUpState.value = AuthState.Error("Password must be at least 6 characters")
            return false
        }
        return true
    }
}

sealed class AuthState {
    object Loading                    : AuthState()
    data class Success(val user: User): AuthState()
    data class Error(val message: String) : AuthState()
}