package id.dimas.fiazenify.data.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import id.dimas.fiazenify.data.model.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "transaction_tbl")
data class TransactionEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "uid")
    val uid: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "amount")
    val amount: Int,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name = "created")
    val created: Timestamp
) : Parcelable {

    fun toDomain(): Transaction {
        return Transaction(
            id,
            uid,
            title,
            type,
            category,
            amount,
            note,
            created
        )
    }
}