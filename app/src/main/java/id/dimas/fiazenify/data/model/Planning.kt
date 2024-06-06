package id.dimas.fiazenify.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Planning(
    val id: String = "",
    val uid: String = "",
    val title: String = "",
    val currentAmount: Int = 0,
    val percent: Double = 0.0,
    val targetAmount: Int = 0,
    val note: String = "",
) : Parcelable
