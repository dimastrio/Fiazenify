package id.dimas.fiazenify.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.dimas.fiazenify.data.room.AppDatabase
import id.dimas.fiazenify.data.room.dao.CategoryDao
import id.dimas.fiazenify.data.room.dao.PlanningDao
import id.dimas.fiazenify.data.room.dao.TransactionDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "Appdatabase.db").build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun providePlanningDao(database: AppDatabase): PlanningDao {
        return database.planningDao()
    }

}