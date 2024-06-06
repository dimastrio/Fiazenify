package id.dimas.fiazenify.data.repositoryImp

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bumptech.glide.load.HttpException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.dimas.fiazenify.data.model.Category
import id.dimas.fiazenify.data.model.Statistic
import id.dimas.fiazenify.data.model.Transaction
import id.dimas.fiazenify.data.repository.TransactionRepository
import id.dimas.fiazenify.data.room.dao.CategoryDao
import id.dimas.fiazenify.data.room.dao.TransactionDao
import id.dimas.fiazenify.data.room.entity.CategoryEntity
import id.dimas.fiazenify.data.room.entity.TransactionEntity
import id.dimas.fiazenify.util.Result
import id.dimas.fiazenify.util.stringToTimestamp
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class TransactionRepositoryImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
) : TransactionRepository {


    override fun addTransaction(
        id: String,
        uid: String,
        title: String,
        type: String,
        category: String,
        amount: Int,
        note: String,
        created: Timestamp?
    ): LiveData<Result<String>> = liveData {
        emit(Result.Loading())
        try {
            val docId = database.collection("transaction").document().id
            val transaction = TransactionEntity(
                id = docId,
                uid = auth.currentUser!!.uid,
                title = title,
                type = type,
                category = category,
                amount = amount,
                note = note,
                created = Timestamp.now()
            )

            database.collection("transaction")
                .document(docId)
                .set(transaction)

            transactionDao.insertTransaction(transaction)

            emit(Result.Success("Success"))

        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun getTransactionLimit(): LiveData<Result<List<Transaction>>> = liveData {
        emit(Result.Loading())

        val uid = auth.currentUser?.uid
        val oldData = transactionDao.getTransactionLimit(uid!!).map { it.toDomain() }
        emit(Result.Loading(oldData))

        try {
            val transactionList = ArrayList<TransactionEntity>()
            val result = database.collection("transaction")
                .orderBy("created", Query.Direction.DESCENDING)
                .whereEqualTo("uid", auth.currentUser?.uid)
                .limit(10)
                .get()
                .await()

            result.documents.forEach {
                if (it.data != null) {
                    transactionList.add(
                        TransactionEntity(
                            id = it.id,
                            uid = auth.currentUser!!.uid,
                            title = it.data!!["title"].toString(),
                            type = it.data!!["type"].toString(),
                            category = it.data!!["category"].toString(),
                            amount = it.data!!["amount"].toString().toInt(),
                            note = it.data!!["note"].toString(),
                            created = it.data!!["created"] as Timestamp
                        )
                    )
                }
            }


//            for (list in transactionList) {
//                transactionListRoom.add(
//                    TransactionEntity(
//                        id = list.id,
//                        uid = list.uid,
//                        title = list.title,
//                        type = list.type,
//                        category = list.category,
//                        amount = list.amount,
//                        note = list.note,
//                        created = list.created!!
//                    )
//                )
//            }

            if (result.documents.isNotEmpty()) {
                transactionDao.deleteListTransaction(uid)
                transactionDao.insertListTransaction(transactionList)
                val newData = transactionDao.getTransactionLimit(uid).map { it.toDomain() }
                emit(Result.Success(newData))
            }

        } catch (e: HttpException) {
            emit(Result.Error(e.localizedMessage, oldData))
        }
        val newData = transactionDao.getTransactionLimit(uid).map { it.toDomain() }
        emit(Result.Success(newData))


    }

    override fun getCategory(): LiveData<Result<List<Category>>> = liveData {
        emit(Result.Loading())

        val oldData = categoryDao.getCategory().map { it.toDomain() }
        emit(Result.Loading(oldData))

        try {
            val categoryList = ArrayList<CategoryEntity>()
            val result = database.collection("category")
                .orderBy("id", Query.Direction.ASCENDING)
                .get()
                .await()


            result.documents.forEach {
                if (it.data != null) {
                    categoryList.add(
                        CategoryEntity(
                            id = it.data!!["id"].toString().toInt(),
                            name = it.data!!["name"].toString()
                        )
                    )
                }
            }


            if (result.documents.isNotEmpty()) {
                categoryDao.deleteListCategory()
                categoryDao.insertListCategory(categoryList)
            }

//            emit(Result.Success(categoryList))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }

        val newData = categoryDao.getCategory().map { it.toDomain() }
        emit(Result.Success(newData))
    }

    override fun updateTransaction(
        id: String,
        title: String,
        type: String,
        category: String,
        amount: Int,
        note: String
    ): LiveData<Result<String>> = liveData {
        emit(Result.Loading())
        try {
//            val transactionUpdate = TransactionEntity(id, auth.currentUser?.uid!!, title, type, category, amount, note)

            database.collection("transaction")
                .document(id)
                .update(
                    mapOf(
                        "title" to title,
                        "type" to type,
                        "category" to category,
                        "amount" to amount,
                        "note" to note
                    )
                )

            transactionDao.updateTransactionById(id, title, type, category, amount, note)

            emit(Result.Success("Success"))

        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun getTransactionById(id: String): LiveData<Result<Transaction>> = liveData {
        emit(Result.Loading())

        try {
            val transaction = transactionDao.getTransactionById(id).toDomain()
//            val result = database.collection("transaction")
//                .document(id)
//                .get()
//                .await()
//
////            result.addOnCompleteListener {
////                val document: DocumentSnapshot = it.result
////                if (document.exists()){
////                    transaction= document.toObject(Transaction::class.java)!!
////                }
////            }
//            val transaction: Transaction = result.toObject(Transaction::class.java)!!

            emit((Result.Success(transaction)))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun deleteTransactionById(id: String): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading())

        transactionDao.deleteTransaction(id)

        try {
            val result = database.collection("transaction")
                .document(id)
                .delete()

            emit(Result.Success(result.isSuccessful))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun getTransactionIn(): LiveData<Result<Long>> = liveData {
        emit(Result.Loading())

        try {

            var transactionIn = 0L
            val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            val date = Date()
            val uid = auth.currentUser?.uid
            val dateStart = stringToTimestamp("01/${dateFormat.format(date)} 00:00:00")?.time
            val dateEnd = stringToTimestamp("31/${dateFormat.format(date)} 23:59:00")?.time
            val dataTransactionIn = transactionDao.getTransactionIn(uid!!, dateStart!!, dateEnd!!)
            for (i in dataTransactionIn) {
                transactionIn += i
            }
            emit(Result.Success(transactionIn))


        } catch (e: HttpException) {
            emit(Result.Error("Unknow Error"))
        }


    }

    override fun getTransactionOut(): LiveData<Result<Long>> = liveData {
        emit(Result.Loading())
        try {

            var transactionOut = 0L
            val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            val date = Date()
            val uid = auth.currentUser?.uid
            val dateStart = stringToTimestamp("01/${dateFormat.format(date)} 00:00:00")?.time
            val dateEnd = stringToTimestamp("31/${dateFormat.format(date)} 23:59:00")?.time
            val dataTransactionOut = transactionDao.getTransactionOut(uid!!, dateStart!!, dateEnd!!)
            for (i in dataTransactionOut) {
                transactionOut += i
            }

            emit(Result.Success(transactionOut.toLong()))

        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun getTransactionTotal(): LiveData<Result<Long>> = liveData {
        emit(Result.Loading())
        try {

            val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            val date = Date()

            var transactionIn = 0L
            var transactionOut = 0L
            val uid = auth.currentUser?.uid
            val dateStart = stringToTimestamp("01/${dateFormat.format(date)} 00:00:00")?.time
            val dateEnd = stringToTimestamp("31/${dateFormat.format(date)} 23:59:00")?.time
            val dataTransactionIn = transactionDao.getTransactionIn(uid!!, dateStart!!, dateEnd!!)
            val dataTransactionOut = transactionDao.getTransactionOut(uid, dateStart, dateEnd)

            for (i in dataTransactionIn) {
                transactionIn += i
            }

            for (i in dataTransactionOut) {
                transactionOut += i
            }

            val transactionTotal = transactionIn.toLong() - transactionOut.toLong()

            emit(Result.Success(transactionTotal))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun getMonthTransaction(): LiveData<Result<String>> = liveData {
        emit(Result.Loading())
        try {

            val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            val date = Date()

            emit(Result.Success("*Data bulan ${dateFormat.format(date)}"))
        } catch (e: Exception) {
            emit(Result.Error("Unknow Error"))
        }
    }

    override fun getStatistic(): LiveData<Result<List<Statistic>>> = liveData {
        emit(Result.Loading())

//    try {
//        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
//        val date = Date()
//        val filter = Filter.and(
//            Filter.greaterThanOrEqualTo(
//                "created",
//                stringToTimestamp("01/${dateFormat.format(date)} 00:00")
//            ),
//            Filter.lessThanOrEqualTo(
//                "created",
//                stringToTimestamp("31/${dateFormat.format(date)} 23:59")
//            ),
//        )
//
//        val categoryList = arrayListOf<Category>()
//        val statisticList = arrayListOf<Statistic>()
//        val getAllCategory = database.collection("category")
//            .orderBy("id", Query.Direction.ASCENDING)
//            .get()
//            .await()
//
//        getAllCategory.documents.forEach {
//            if (it != null) {
//                categoryList.add(
//                    Category(
//                        id = it.data!!["id"].toString().toInt(),
//                        name = it.data!!["name"].toString()
//                    )
//                )
//            }
//        }
//
//        categoryList.forEach {
//            if (it != null) {
//                val result = database.collection("transaction")
//                    .whereEqualTo("uid", auth.currentUser?.uid)
//                    .whereEqualTo("category", it.name)
//                    .where(filter)
//                    .get()
//                    .await()
//
//                if (result.documents.isNotEmpty()) {
//                    statisticList.add(
//                        Statistic(
//                            name = it.name,
//                            totalData = result.documents.size.toFloat()
//                        )
//                    )
//                }
//
//            }
//        }
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val date = Date()
        val dateStart = stringToTimestamp("01/${dateFormat.format(date)} 00:00:00")?.time
        val dateEnd = stringToTimestamp("31/${dateFormat.format(date)} 23:59:59")?.time

        val statisticList = ArrayList<Statistic>()
        val uid = auth.currentUser?.uid
        val getAllCategory = categoryDao.getCategory().map { it.toDomain() }
        getAllCategory.forEach {

            val result = transactionDao.getStatistic(uid!!, it.name, dateStart!!, dateEnd!!)
            if (result.isNotEmpty()) {
                statisticList.add(
                    Statistic(
                        name = it.name,
                        totalData = result.size.toFloat()
                    )
                )
            }
        }

        emit(Result.Success(statisticList))


//        val categoryHiburan = database.collection("transaction")
//            .whereEqualTo("uid", auth.currentUser?.uid)
//            .whereEqualTo("category", "Hiburan")
//            .where(filter)
//            .get()
//            .await()
//
//        val categoryTransportasi = database.collection("transaction")
//            .whereEqualTo("uid", auth.currentUser?.uid)
//            .whereEqualTo("category", "Transportasi")
//            .where(filter)
//            .get()
//            .await()
//
//        val categoryTagihan = database.collection("transaction")
//            .whereEqualTo("uid", auth.currentUser?.uid)
//            .whereEqualTo("category", "Tagihan")
//            .where(filter)
//            .get()
//            .await()
//
//        val categoryLain = database.collection("transaction")
//            .whereEqualTo("uid", auth.currentUser?.uid)
//            .whereEqualTo("category", "Lain-Lain")
//            .where(filter)
//            .get()
//            .await()

//            val transactionResult = arrayListOf(
//                categoryHiburan.documents.size,
//                categoryTransportasi.documents.size,
//                categoryTagihan.documents.size,
//                categoryLain.documents.size
//            )
//
//
//            emit(Result.Success(transactionResult))

//    } catch (e: Exception) {
//        emit(Result.Error(e.localizedMessage))
//    }
    }

    override fun getTransaction(): LiveData<Result<List<Transaction>>> = liveData {
        emit(Result.Loading())

        val uid = auth.currentUser?.uid
        val oldData = transactionDao.getTransaction(uid!!).map { it.toDomain() }
        emit(Result.Loading(oldData))

        if (oldData.isEmpty()) {
            try {
                val transactionList = ArrayList<TransactionEntity>()
                val result = database.collection("transaction")
                    .orderBy("created", Query.Direction.DESCENDING)
                    .whereEqualTo("uid", auth.currentUser?.uid)
                    .get()
                    .await()

                result.documents.forEach {
                    if (it.data != null) {
                        transactionList.add(
                            TransactionEntity(
                                id = it.id,
                                uid = auth.currentUser!!.uid,
                                title = it.data!!["title"].toString(),
                                type = it.data!!["type"].toString(),
                                category = it.data!!["category"].toString(),
                                amount = it.data!!["amount"].toString().toInt(),
                                note = it.data!!["note"].toString(),
                                created = it.data!!["created"] as Timestamp
                            )
                        )
                    }
                }


                if (result.documents.isNotEmpty()) {
                    transactionDao.deleteListTransaction(uid)
                    transactionDao.insertListTransaction(transactionList)
                }


            } catch (e: HttpException) {
                emit(Result.Error(e.localizedMessage, oldData))
            }
        } else {

            val newData = transactionDao.getTransaction(uid).map { it.toDomain() }
            emit(Result.Success(newData))
        }
    }

    override fun getTransactionByDate(
        dateStart: String,
        dateEnd: String
    ): LiveData<Result<List<Transaction>>> = liveData {
        emit(Result.Loading())
        try {
//            val transactionList = ArrayList<Transaction>()
//            val result = database.collection("transaction")
//                .orderBy("created", Query.Direction.DESCENDING)
//                .whereEqualTo("uid", auth.currentUser?.uid)
//                .whereGreaterThanOrEqualTo("created", stringToTimestamp("$dateStart 00:00")!!)
//                .whereLessThanOrEqualTo("created", stringToTimestamp("$dateEnd 23:59")!!)
//                .get()
//                .await()
//
//            result.documents.forEach {
//                if (it.data != null) {
//                    transactionList.add(
//                        Transaction(
//                            id = it.id,
//                            uid = auth.currentUser!!.uid,
//                            title = it.data!!["title"].toString(),
//                            type = it.data!!["type"].toString(),
//                            category = it.data!!["category"].toString(),
//                            amount = it.data!!["amount"].toString().toInt(),
//                            note = it.data!!["note"].toString(),
//                            created = it.data!!["created"] as Timestamp
//                        )
//                    )
//                }
//            }

//            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//            val date = Date()
            val uid = auth.currentUser?.uid
            val start = stringToTimestamp(dateStart)?.time
            val end = stringToTimestamp(dateEnd)?.time

            val data =
                transactionDao.getTransactionByDate(uid!!, start!!, end!!).map { it.toDomain() }

            emit(Result.Success(data))

        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage))
        }
    }
}