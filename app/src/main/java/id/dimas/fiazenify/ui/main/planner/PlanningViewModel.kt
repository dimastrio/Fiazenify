package id.dimas.fiazenify.ui.main.planner

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.dimas.fiazenify.data.repository.PlanningRepository
import javax.inject.Inject

@HiltViewModel
class PlanningViewModel @Inject constructor(
    private val repository: PlanningRepository
) : ViewModel() {

    fun addPlanning(
        id: String,
        uid: String,
        title: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        note: String
    ) = repository.addPlanning(id, uid, title, currentAmount, percent, targetAmount, note)

    fun getPlanning() = repository.getPlanning()

    fun increaseCurrentAmount(
        id: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        newCurrentAmount: Int
    ) = repository.increaseCurrentAmount(id, currentAmount, percent, targetAmount, newCurrentAmount)

    fun decreaseCurrentAmount(
        id: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        newCurrentAmount: Int
    ) = repository.decreaseCurrentAmount(id, currentAmount, percent, targetAmount, newCurrentAmount)

    fun updatePlanning(id: String, title: String, targetAmount: Int, note: String) =
        repository.updatePlanning(id, title, targetAmount, note)

    fun getPlanningById(id: String) = repository.getPlanningById(id)

    fun deletePlanningById(id: String) = repository.deletePlanningById(id)


}