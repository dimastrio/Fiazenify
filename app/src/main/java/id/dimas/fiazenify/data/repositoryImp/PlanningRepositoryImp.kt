package id.dimas.fiazenify.data.repositoryImp

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.dimas.fiazenify.data.model.Planning
import id.dimas.fiazenify.data.repository.PlanningRepository
import id.dimas.fiazenify.data.room.dao.PlanningDao
import id.dimas.fiazenify.data.room.entity.PlanningEntity
import id.dimas.fiazenify.util.Result
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class PlanningRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val planningDao: PlanningDao
) : PlanningRepository {


    override fun addPlanning(
        id: String,
        uid: String,
        title: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        note: String
    ): LiveData<Result<String>> = liveData {
        emit(Result.Loading())
        try {
            val docId = database.collection("planning").document().id
            val planning = PlanningEntity(
                id = docId,
                uid = auth.currentUser!!.uid,
                title = title,
                currentAmount = currentAmount,
                percent = percent,
                targetAmount = targetAmount,
                note = note
            )


            database.collection("planning")
                .document(docId)
                .set(planning)

            planningDao.insertPlanning(planning)

            emit(Result.Success("Success"))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun getPlanning(): LiveData<Result<List<Planning>>> = liveData {
        emit(Result.Loading())

        val uid = auth.currentUser?.uid
        val oldData = planningDao.getAllPlanning(uid!!).map { it.toDomain() }
        emit(Result.Loading(oldData))

        try {

            val planningList = ArrayList<PlanningEntity>()
            val result = database.collection("planning")
                .orderBy("percent", Query.Direction.DESCENDING)
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .await()

            result.documents.forEach {
                if (it.data != null) {
                    planningList.add(
                        PlanningEntity(
                            id = it.id,
                            uid = auth.currentUser!!.uid,
                            title = it.data!!["title"].toString(),
                            currentAmount = it.data!!["currentAmount"].toString().toInt(),
                            percent = it.data!!["percent"].toString().toDouble(),
                            targetAmount = it.data!!["targetAmount"].toString().toInt(),
                            note = it.data!!["note"].toString()
                        )
                    )
                }
            }

            if (result.documents.isNotEmpty()) {
                planningDao.deleteListPlanning(uid)
                planningDao.insertListPlanning(planningList)
                val newData = planningDao.getAllPlanning(uid).map { it.toDomain() }
                emit(Result.Success(newData))
            }

        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
        val newData = planningDao.getAllPlanning(uid).map { it.toDomain() }
        emit(Result.Success(newData))
    }

    override fun increaseCurrentAmount(
        id: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        newCurrentAmount: Int
    ): LiveData<Result<String>> =
        liveData {
            emit(Result.Loading())
            try {

                val updateAmount = currentAmount + newCurrentAmount
                val updatePercent = (updateAmount / (targetAmount.toDouble())) * 100

                database.collection("planning")
                    .document(id)
                    .update(
                        mapOf(
                            "currentAmount" to updateAmount,
                            "percent" to updatePercent,
                        )
                    )

                planningDao.updateCurrentAmountPlanning(id, updateAmount, updatePercent)

                emit(Result.Success("Success"))
            } catch (e: Exception) {
                emit(Result.Error("Unknow Error"))
            }
        }

    override fun decreaseCurrentAmount(
        id: String,
        currentAmount: Int,
        percent: Double,
        targetAmount: Int,
        newCurrentAmount: Int
    ): LiveData<Result<String>> = liveData {
        emit(Result.Loading())
        try {
            val updateAmount = currentAmount - newCurrentAmount
            val updatePercent = (updateAmount / (targetAmount.toDouble())) * 100

            database.collection("planning")
                .document(id)
                .update(
                    mapOf(
                        "currentAmount" to updateAmount,
                        "percent" to updatePercent,
                    )
                )

            planningDao.updateCurrentAmountPlanning(id, updateAmount, updatePercent)

            emit(Result.Success("Success"))

        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun updatePlanning(
        id: String,
        title: String,
        targetAmount: Int,
        note: String
    ): LiveData<Result<String>> = liveData {
        emit(Result.Loading())
        try {
//            val result = database.collection("planning").document(id).get().await()
//            val currentAmount = result.data!!["currentAmount"].toString().toInt()
//            database.collection("planning")
//                .document(id)
//                .get()
////                .addOnSuccessListener { result ->
////                    currentAmount = result.data!!["currentAmount"].toString().toInt()
////                }
            val currentAmount = planningDao.getPlanningById(id).toDomain().currentAmount
//
            val updatePercent = (currentAmount / (targetAmount.toDouble())) * 100
            database.collection("planning")
                .document(id)
                .update(
                    mapOf(
                        "title" to title,
                        "percent" to updatePercent,
                        "targetAmount" to targetAmount,
                        "note" to note
                    )
                )

            planningDao.updatePlanningById(id, title, updatePercent, targetAmount, note)

//            val planningUpdate = Planning(id, auth.currentUser!!.uid, title, currentAmount, updatePercent, targetAmount, note)
            emit(Result.Success("Rencana Berhasil Diubah"))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun getPlanningById(id: String): LiveData<Result<Planning>> = liveData {
        emit(Result.Loading())
        try {

            val planning = planningDao.getPlanningById(id).toDomain()

//            val result = database.collection("planning")
//                .document(id)
//                .get()
//                .await()
//
//            val planning: Planning = result.toObject(Planning::class.java)!!

            emit(Result.Success(planning))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun deletePlanningById(id: String): LiveData<Result<String>> = liveData {
        emit(Result.Loading())

        planningDao.deletePlanningById(id)
        try {

            database.collection("planning")
                .document(id)
                .delete()

            emit(Result.Success("Rencana Berhasil Dihapus"))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }
}