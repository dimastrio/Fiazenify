package id.dimas.fiazenify.ui.main.transaction

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dimas.fiazenify.data.repository.TransactionRepository
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {


    fun addTransaction(
        id: String,
        uid: String,
        title: String,
        type: String,
        category: String,
        amount: Int,
        note: String,
        created: Timestamp
    ) = repository.addTransaction(id, uid, title, type, category, amount, note, created)

    fun getCategory() = repository.getCategory()

    fun getTransactionById(id: String) = repository.getTransactionById(id)

    fun updateTransaction(
        id: String,
        title: String,
        type: String,
        category: String,
        amount: Int,
        note: String
    ) = repository.updateTransaction(id, title, type, category, amount, note)

    fun getTransaction() = repository.getTransaction()

    fun deleteTransaction(id: String) = repository.deleteTransactionById(id)

    fun getTransactionByDate(dateStart: String, dateEnd: String) =
        repository.getTransactionByDate(dateStart, dateEnd)
}