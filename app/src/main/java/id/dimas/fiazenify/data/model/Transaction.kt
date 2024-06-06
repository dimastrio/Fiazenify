package id.dimas.fiazenify.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val id: String = "",
    val uid: String = "",
    val title: String = "",
    val type: String = "",
    val category: String = "",
    val amount: Int = 0,
    val note: String = "",
    val created: Timestamp? = Timestamp.now()
) : Parcelable