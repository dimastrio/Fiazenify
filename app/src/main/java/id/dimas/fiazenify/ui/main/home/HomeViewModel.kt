package id.dimas.fiazenify.ui.main.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dimas.fiazenify.data.repository.AuthRepository
import id.dimas.fiazenify.data.repository.TransactionRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val userRepo: AuthRepository
) : ViewModel() {

    fun getTransaction() = repository.getTransactionLimit()

//    fun getTransactionById(id: String) = repository.getTransactionById(id)

    fun deleteTransaction(id: String) = repository.deleteTransactionById(id)

    fun getTransactionIn() = repository.getTransactionIn()

    fun getTransactionOut() = repository.getTransactionOut()

    fun getTransactionTotal() = repository.getTransactionTotal()

    fun getMonthTransaction() = repository.getMonthTransaction()

}