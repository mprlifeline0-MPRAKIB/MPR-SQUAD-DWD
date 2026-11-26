package com.mprsquad.dwd.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Offline-first schema. Personal ledger rows live in their own table and are
 * intentionally absent from every work/dashboard query.
 */
@Entity(tableName = "duties", indices = [Index("workDate"), Index("location")])
data class DutyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workDate: Long,
    val startTime: String,
    val endTime: String,
    val location: String,
    val dutyType: String, // DUTY or DAY_OFF
    val description: String = ""
)

@Entity(tableName = "work_records", indices = [Index("recordDate"), Index("location"), Index("recordType")])
data class WorkRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recordDate: Long,
    val location: String,
    val recordType: String, // BILL, EXPENSE, STOCK, CONTRACT
    val title: String,
    val description: String = "",
    val amount: Double = 0.0,
    val outboundMode: String = "",
    val outboundFare: Double = 0.0,
    val returnMode: String = "",
    val returnFare: Double = 0.0,
    val mealDay: Double = 0.0,
    val mealNight: Double = 0.0,
    val peopleCount: Int = 0,
    val peopleNames: String = "",
    val payer: String = "",
    val paid: Boolean = false
)

@Entity(tableName = "people", indices = [Index("displayName")])
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val displayName: String,
    val personCode: String = "",
    val photoUri: String? = null,
    val notes: String = ""
)

/** Private person-to-person ledger; never joined into work_records. */
@Entity(tableName = "person_transactions", indices = [Index("personId"), Index("transactionDate")])
data class PersonTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personId: Long,
    val transactionDate: Long,
    val amount: Double,
    val description: String = "",
    val received: Boolean = false,
    val comments: String = ""
)

@Dao
interface DwdDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDuty(item: DutyEntity): Long

    @Update
    suspend fun updateDuty(item: DutyEntity)

    @Query("DELETE FROM duties WHERE id = :id")
    suspend fun deleteDuty(id: Long)

    @Query("SELECT * FROM duties ORDER BY workDate DESC")
    fun observeDuties(): Flow<List<DutyEntity>>

    @Query("SELECT * FROM duties WHERE workDate BETWEEN :from AND :to ORDER BY workDate")
    fun observeDuties(from: Long, to: Long): Flow<List<DutyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWorkRecord(item: WorkRecordEntity): Long

    @Update
    suspend fun updateWorkRecord(item: WorkRecordEntity)

    @Query("DELETE FROM work_records WHERE id = :id")
    suspend fun deleteWorkRecord(id: Long)

    @Query("SELECT * FROM work_records WHERE recordType = :type ORDER BY recordDate DESC")
    fun observeWorkRecords(type: String): Flow<List<WorkRecordEntity>>

    /** Dashboard scope is strictly work_records; personal tables cannot leak here. */
    @Query("SELECT * FROM work_records WHERE recordDate BETWEEN :from AND :to ORDER BY recordDate DESC")
    fun observeDashboard(from: Long, to: Long): Flow<List<WorkRecordEntity>>

    @Query("SELECT COALESCE(SUM(amount + outboundFare + returnFare + mealDay + mealNight), 0.0) FROM work_records WHERE recordDate BETWEEN :from AND :to")
    fun dashboardTotal(from: Long, to: Long): Flow<Double>

    @Query("SELECT * FROM work_records WHERE location = :location ORDER BY recordDate DESC")
    fun observeLocation(location: String): Flow<List<WorkRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePerson(person: PersonEntity): Long

    @Update
    suspend fun updatePerson(person: PersonEntity)

    @Query("DELETE FROM people WHERE id = :id")
    suspend fun deletePerson(id: Long)

    @Query("SELECT * FROM people ORDER BY displayName COLLATE NOCASE")
    fun observePeople(): Flow<List<PersonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePersonTransaction(item: PersonTransactionEntity): Long

    @Update
    suspend fun updatePersonTransaction(item: PersonTransactionEntity)

    @Query("DELETE FROM person_transactions WHERE id = :id")
    suspend fun deletePersonTransaction(id: Long)

    @Query("SELECT * FROM person_transactions WHERE personId = :personId ORDER BY transactionDate DESC")
    fun observePersonTransactions(personId: Long): Flow<List<PersonTransactionEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM person_transactions WHERE personId = :personId AND received = 0")
    fun personOutstanding(personId: Long): Flow<Double>
}

@Database(
    entities = [DutyEntity::class, WorkRecordEntity::class, PersonEntity::class, PersonTransactionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class DwdDatabase : RoomDatabase() {
    abstract fun dwdDao(): DwdDao

    companion object {
        @Volatile private var INSTANCE: DwdDatabase? = null

        fun get(context: Context): DwdDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DwdDatabase::class.java,
                    "mpr_squad_dwd.db"
                ).build().also { INSTANCE = it }
            }
    }
}
