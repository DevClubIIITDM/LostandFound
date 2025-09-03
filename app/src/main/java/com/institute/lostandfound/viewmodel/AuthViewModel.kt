package com.institute.lostandfound.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.institute.lostandfound.data.model.User
import com.institute.lostandfound.repository.FirebaseRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    init {
        checkCurrentUser()
    }
    
    private fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                val userId = repository.getCurrentUserId()
                if (userId != null) {
                    val user = repository.getUser(userId)
                    if (user.isSuccess && user.getOrNull() != null) {
                        _currentUser.value = user.getOrNull()
                        _authState.value = AuthState.Success
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to check current user: ${e.message}")
            }
        }
    }
    
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                val authResult = repository.signInWithGoogle(idToken)
                if (authResult.isSuccess) {
                    val userId = authResult.getOrNull() ?: return@launch
                    
                    // Check if user already exists
                    val existingUser = repository.getUser(userId)
                    if (existingUser.isSuccess && existingUser.getOrNull() != null) {
                        _currentUser.value = existingUser.getOrNull()
                        _authState.value = AuthState.Success
                    } else {
                        // Create new user profile
                        val newUser = User(
                            id = userId,
                            name = "Google User",
                            email = "",
                            phone = ""
                        )
                        
                        val createResult = repository.createUser(newUser)
                        if (createResult.isSuccess) {
                            _currentUser.value = newUser
                            _authState.value = AuthState.Success
                        } else {
                            _authState.value = AuthState.Error("Failed to create user profile")
                        }
                    }
                } else {
                    _authState.value = AuthState.Error("Google sign in failed: ${authResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Google sign in error: ${e.message}")
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                repository.signOut()
                _currentUser.value = null
                _authState.value = AuthState.SignedOut
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Sign out error: ${e.message}")
            }
        }
    }
    
    sealed class AuthState {
        object Loading : AuthState()
        object Success : AuthState()
        object SignedOut : AuthState()
        data class Error(val message: String) : AuthState()
    }
} 
