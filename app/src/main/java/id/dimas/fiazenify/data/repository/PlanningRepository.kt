package id.dimas.fiazenify.data.repository

import androidx.lifecycle.LiveData
import id.dimas.fiazenify.data.model.Planning
import id.dimas.fiazenify.util.Result

interface PlanningRepository {

    fun addPlanning(
        id: String,
        uid: String,
        title: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        note: String
    ): LiveData<Result<String>>

    fun getPlanning(): LiveData<Result<List<Planning>>>

    fun increaseCurrentAmount(
        id: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        newCurrentAmount: Int
    ): LiveData<Result<String>>

    fun decreaseCurrentAmount(
        id: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        newCurrentAmount: Int
    ): LiveData<Result<String>>

    fun updatePlanning(
        id: String,
        title: String,
        targetAmount: Int,
        note: String
    ): LiveData<Result<String>>

    fun getPlanningById(id: String): LiveData<Result<Planning>>

    fun deletePlanningById(id: String): LiveData<Result<String>>

}