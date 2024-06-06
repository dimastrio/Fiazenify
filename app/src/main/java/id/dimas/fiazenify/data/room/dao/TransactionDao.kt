package id.dimas.fiazenify.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.dimas.fiazenify.data.room.entity.TransactionEntity

@Dao
interface TransactionDao {

    // Transaction
    @Query("SELECT * FROM transaction_tbl WHERE uid = :uid ORDER BY created DESC")
    suspend fun getTransaction(uid: String): List<TransactionEntity>

    @Query("SELECT * FROM transaction_tbl WHERE uid = :uid ORDER BY created DESC LIMIT 10 ")
    suspend fun getTransactionLimit(uid: String): List<TransactionEntity>

    @Query("SELECT * FROM transaction_tbl WHERE uid = :uid AND created >= :dateStart AND created <= :dateEnd ORDER BY created DESC")
    suspend fun getTransactionByDate(
        uid: String,
        dateStart: Long,
        dateEnd: Long
    ): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListTransaction(transaction: List<TransactionEntity>)

    @Query("DELETE FROM transaction_tbl WHERE uid = :uid")
    suspend fun deleteListTransaction(uid: String)

    @Query("DELETE FROM transaction_tbl WHERE id = :id")
    suspend fun deleteTransaction(id: String)

    @Query("SELECT amount FROM transaction_tbl WHERE uid = :uid AND type = 'Pemasukan' AND created >= :dateStart AND created <= :dateEnd")
    suspend fun getTransactionIn(uid: String, dateStart: Long, dateEnd: Long): List<Int>

    @Query("SELECT amount FROM transaction_tbl WHERE uid = :uid AND type = 'Pengeluaran' AND created >= :dateStart AND created <= :dateEnd")
    suspend fun getTransactionOut(uid: String, dateStart: Long, dateEnd: Long): List<Int>

    @Query("SELECT * FROM transaction_tbl WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity

    @Query("UPDATE transaction_tbl set title =:title, type =:type, category =:category, amount =:amount, note =:note WHERE id =:id")
    suspend fun updateTransactionById(
        id: String,
        title: String,
        type: String,
        category: String,
        amount: Int,
        note: String
    )

    // Statistic
    @Query("SELECT * FROM transaction_tbl WHERE uid =:uid AND category = :category AND created >= :dateStart AND created <= :dateEnd")
    suspend fun getStatistic(
        uid: String,
        category: String,
        dateStart: Long,
        dateEnd: Long
    ): List<TransactionEntity>


}