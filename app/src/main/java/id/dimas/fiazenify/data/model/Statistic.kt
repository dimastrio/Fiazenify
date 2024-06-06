package id.dimas.fiazenify.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Statistic(
    val name: String,
    val totalData: Float
) : Parcelable
