package id.dimas.fiazenify.ui.main.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dimas.fiazenify.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

//    fun getUser() = repository.getUser()


    fun updateProfilePhoto(uri: Uri) = repository.updateProfilePhoto(uri)

    fun changePassword(oldPass: String, newPass: String) =
        repository.changePassword(oldPass, newPass)

    fun changeUsername(newUsername: String) = repository.changeUsername(newUsername)

}