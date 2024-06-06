package id.dimas.fiazenify.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.dimas.fiazenify.data.repository.AuthRepository
import id.dimas.fiazenify.data.repository.PlanningRepository
import id.dimas.fiazenify.data.repository.TransactionRepository
import id.dimas.fiazenify.data.repositoryImp.AuthRepositoryImp
import id.dimas.fiazenify.data.repositoryImp.PlanningRepositoryImp
import id.dimas.fiazenify.data.repositoryImp.TransactionRepositoryImp
import id.dimas.fiazenify.data.room.dao.CategoryDao
import id.dimas.fiazenify.data.room.dao.PlanningDao
import id.dimas.fiazenify.data.room.dao.TransactionDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTransactionRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
        transactionDao: TransactionDao,
        categoryDao: CategoryDao
    ): TransactionRepository {
        return TransactionRepositoryImp(auth, database, transactionDao, categoryDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): AuthRepository {
        return AuthRepositoryImp(auth, storage)
    }

    @Provides
    @Singleton
    fun providePlanningRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        planningDao: PlanningDao
    ): PlanningRepository {
        return PlanningRepositoryImp(auth, database, planningDao)
    }
}