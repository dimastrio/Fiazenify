package id.dimas.fiazenify.data.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import id.dimas.fiazenify.data.model.Planning
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "planning_tbl")
data class PlanningEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "uid")
    val uid: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "current_amount")
    val currentAmount: Int,

    @ColumnInfo(name = "percent")
    val percent: Double,

    @ColumnInfo(name = "target_amount")
    val targetAmount: Int,

    @ColumnInfo(name = "note")
    val note: String,

    ) : Parcelable {

    fun toDomain(): Planning {
        return Planning(
            id,
            uid,
            title,
            currentAmount,
            percent,
            targetAmount,
            note
        )
    }
}
