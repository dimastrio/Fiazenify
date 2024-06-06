package id.dimas.fiazenify.data.repositoryImp

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import id.dimas.fiazenify.data.repository.AuthRepository
import id.dimas.fiazenify.util.Result
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : AuthRepository {

    override fun registerUser(
        username: String,
        email: String,
        password: String
    ): LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading())
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
//                .addOnSuccessListener {
//                    val uid = auth.currentUser?.uid
//                    val user = User(uid, username, email)
//                    database.collection("user").add(user)
//                }
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(username).build()
            )?.await()
            emit(Result.Success(result.user))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        } catch (e: NullPointerException) {
            emit(Result.Error("Data Not Found"))
        }
    }

    override fun loginUser(email: String, password: String): LiveData<Result<FirebaseUser>> =
        liveData {
            emit(Result.Loading())
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                emit(Result.Success(result.user))


            } catch (e: Exception) {
                emit(Result.Error("Unknow Error"))
            }


        }

    override fun getUser(): LiveData<Result<FirebaseUser>> = liveData {
        emit(Result.Loading())
        try {


//                .addOnSuccessListener { result ->
//                    result.forEach { doc ->
//                        user = doc.toObject()
//                    }
//                }
//            val user2: User = user1.forEach {
//                it.toObject<User>()
//            }


            emit(Result.Success(auth.currentUser))

        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun logout(): LiveData<Unit> = liveData {
        emit(auth.signOut())
    }

    override fun checkAuth(): LiveData<Boolean> = liveData {
        emit(auth.currentUser != null)
    }


    override fun updateProfilePhoto(uri: Uri): LiveData<Result<String>> = liveData {
        emit(Result.Loading())
        try {
            val imagePath = "users/${auth.currentUser?.uid}/profile.jpg"
//            var downloadUri = ""
//            val photoUri = Uri.parse(imagePath)
            val uploadTask = storage.reference.child(imagePath).putFile(uri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storage.reference.child(imagePath).downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                        .setPhotoUri(task.result)
                        .build()
                    auth.currentUser?.updateProfile(userProfileChangeRequest)
                }
            }

//            photoRef.putFile(uri).addOnSuccessListener {
//                    it.
//                    photoRef.downloadUrl.addOnSuccessListener {
//                        url = it.toString()
//                    }
//
//            }

//            val photoUri = storage.child(imagePath).downloadUrl.result

//            val userProfileChangeRequest = UserProfileChangeRequest.Builder()
//                .setPhotoUri(photoUri)
//                .build()
//
//
//
//            auth.currentUser?.updateProfile(userProfileChangeRequest)

            emit(Result.Success("Success"))

        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun changePassword(oldPass: String, newPass: String): LiveData<Result<FirebaseUser>> =
        liveData {
            emit(Result.Loading())
            try {
                val emailUser = auth.currentUser?.email.toString()
                val credential: AuthCredential = EmailAuthProvider.getCredential(emailUser, oldPass)
                val result = auth.currentUser?.reauthenticateAndRetrieveData(credential)?.await()


                result?.user?.updatePassword(newPass)


                emit(Result.Success(result?.user))


            } catch (e: FirebaseAuthException) {
                emit(Result.Error("Unknow Error"))

            }

        }

    override fun changeUsername(newUsername: String): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading())
        try {

            val result = auth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(newUsername).build()
            )

            emit(Result.Success(result?.isSuccessful))

        } catch (e: FirebaseException) {
            emit(Result.Error("Unknow Error"))

        }
    }

    override fun forgotPassword(email: String): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading())
        try {
            val result = auth.sendPasswordResetEmail(email)
            emit(Result.Success(result.isSuccessful))
        } catch (e: FirebaseAuthException) {
            emit(Result.Error("Unknow Error"))

        }
    }
}

