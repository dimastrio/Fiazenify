package id.dimas.fiazenify.data.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import id.dimas.fiazenify.data.model.Category
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "category_tbl")
data class CategoryEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String
) : Parcelable {

    fun toDomain(): Category {
        return Category(
            id,
            name
        )
    }
}