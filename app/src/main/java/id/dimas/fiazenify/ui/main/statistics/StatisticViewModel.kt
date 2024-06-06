package id.dimas.fiazenify.ui.main.statistics

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dimas.fiazenify.data.repository.TransactionRepository
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    fun getStatisctic() = repository.getStatistic()
}