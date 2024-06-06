package id.dimas.fiazenify.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.dimas.fiazenify.data.room.entity.PlanningEntity

@Dao
interface PlanningDao {

    // Planning
    @Query("SELECT * FROM planning_tbl WHERE uid =:uid")
    suspend fun getAllPlanning(uid: String): List<PlanningEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListPlanning(planning: List<PlanningEntity>)

    @Query("DELETE FROM planning_tbl WHERE uid= :uid")
    suspend fun deleteListPlanning(uid: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanning(planning: PlanningEntity)

    @Query("DELETE FROM planning_tbl WHERE id= :id")
    suspend fun deletePlanningById(id: String)

    @Query("UPDATE planning_tbl set current_amount =:currentAmount, percent =:percent WHERE id =:id")
    suspend fun updateCurrentAmountPlanning(id: String, currentAmount: Int, percent: Double)

    @Query("SELECT * FROM planning_tbl WHERE id =:id")
    suspend fun getPlanningById(id: String): PlanningEntity

    @Query("UPDATE planning_tbl set title =:title, percent =:percent, target_amount =:targetAmount, note =:note WHERE id =:id")
    suspend fun updatePlanningById(
        id: String,
        title: String,
        percent: Double,
        targetAmount: Int,
        note: String
    )


}
