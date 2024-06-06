package id.dimas.fiazenify.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.dimas.fiazenify.data.room.entity.CategoryEntity

@Dao
interface CategoryDao {

    // Category
    @Query("SELECT * FROM category_tbl ORDER BY id ASC")
    suspend fun getCategory(): List<CategoryEntity>

    @Query("DELETE FROM category_tbl")
    suspend fun deleteListCategory()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListCategory(category: List<CategoryEntity>)

}