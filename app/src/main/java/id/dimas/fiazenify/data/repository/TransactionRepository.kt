package id.dimas.fiazenify.data.repository

import androidx.lifecycle.LiveData
import com.google.firebase.Timestamp
import id.dimas.fiazenify.data.model.Category
import id.dimas.fiazenify.data.model.Statistic
import id.dimas.fiazenify.data.model.Transaction
import id.dimas.fiazenify.util.Result


interface TransactionRepository {

    fun addTransaction(
        id: String,
        uid: String,
        title: String,
        type: String,
        category: String,
        amount: Int,
        note: String,
        created: Timestamp?
    ): LiveData<Result<String>>

    fun getTransactionLimit(): LiveData<Result<List<Transaction>>>

    fun getCategory(): LiveData<Result<List<Category>>>

    fun updateTransaction(
        id: String,
        title: String,
        type: String,
        category: String,
        amount: Int,
        note: String,
    ): LiveData<Result<String>>

    fun getTransactionById(id: String): LiveData<Result<Transaction>>

    fun deleteTransactionById(id: String): LiveData<Result<Boolean>>

    fun getTransactionIn(): LiveData<Result<Long>>

    fun getTransactionOut(): LiveData<Result<Long>>

    fun getTransactionTotal(): LiveData<Result<Long>>

    fun getMonthTransaction(): LiveData<Result<String>>

    fun getStatistic(): LiveData<Result<List<Statistic>>>

    fun getTransaction(): LiveData<Result<List<Transaction>>>

    fun getTransactionByDate(
        dateStart: String,
        dateEnd: String
    ): LiveData<Result<List<Transaction>>>

}