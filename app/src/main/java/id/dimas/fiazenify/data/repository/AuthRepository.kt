package id.dimas.fiazenify.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import id.dimas.fiazenify.util.Result

interface AuthRepository {

    fun registerUser(
        username: String,
        email: String,
        password: String
    ): LiveData<Result<FirebaseUser>>

    fun loginUser(email: String, password: String): LiveData<Result<FirebaseUser>>

    fun getUser(): LiveData<Result<FirebaseUser>>

    fun logout(): LiveData<Unit>

    fun checkAuth(): LiveData<Boolean>

    fun updateProfilePhoto(uri: Uri): LiveData<Result<String>>

    fun changePassword(oldPass: String, newPass: String): LiveData<Result<FirebaseUser>>

    fun changeUsername(newUsername: String): LiveData<Result<Boolean>>

    fun forgotPassword(email: String): LiveData<Result<Boolean>>

}