package id.dimas.fiazenify.ui.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dimas.fiazenify.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {


    fun register(
        username: String,
        email: String,
        password: String
    ) = repository.registerUser(username, email, password)

    fun login(email: String, password: String) = repository.loginUser(email, password)


    fun logout() = repository.logout()

    fun checkAuth() = repository.checkAuth()

    //    val currentUser: FirebaseUser get() = repository.c
    fun getUser() = repository.getUser()

    fun getUsername() = repository.getUser()

    fun forgotPassowrd(email: String) = repository.forgotPassword(email)
}