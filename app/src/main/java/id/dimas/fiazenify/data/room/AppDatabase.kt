package id.dimas.fiazenify.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.dimas.fiazenify.data.room.dao.CategoryDao
import id.dimas.fiazenify.data.room.dao.PlanningDao
import id.dimas.fiazenify.data.room.dao.TransactionDao
import id.dimas.fiazenify.data.room.entity.CategoryEntity
import id.dimas.fiazenify.data.room.entity.PlanningEntity
import id.dimas.fiazenify.data.room.entity.TransactionEntity


@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        PlanningEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    abstract fun categoryDao(): CategoryDao

    abstract fun planningDao(): PlanningDao


}