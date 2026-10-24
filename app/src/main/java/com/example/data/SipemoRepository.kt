package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.CarItem
import com.example.model.LoanRequest
import com.example.model.SipemoConstants
import com.example.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SipemoRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val usersCollection = db.collection("users")
    private val carsCollection = db.collection("cars")
    private val loansCollection = db.collection("loans")

    fun observeAllUsers(): Flow<List<UserProfile>> {
        return usersCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserProfile::class.java)
                }
            }
            .catch { e ->
                Log.e("SipemoRepo", "Error observing all users", e)
                emit(emptyList())
            }
    }

    fun observePendingUsers(): Flow<List<UserProfile>> {
        return usersCollection
            .whereEqualTo("status", SipemoConstants.STATUS_PENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserProfile::class.java)
                }
            }
            .catch { e ->
                Log.e("SipemoRepo", "Error observing pending users", e)
                emit(emptyList())
            }
    }

    suspend fun registerUser(user: UserProfile): Result<String> {
        return try {
            val docRef = if (user.id.isNotBlank()) usersCollection.document(user.id) else usersCollection.document()
            val data = mapOf(
                "fullName" to user.fullName,
                "email" to user.email,
                "phone" to user.phone,
                "unitKerja" to user.unitKerja,
                "jabatan" to user.jabatan,
                "status" to SipemoConstants.STATUS_PENDING,
                "role" to SipemoConstants.ROLE_USER,
                "approvedBy" to "",
                "rejectionReason" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("SipemoRepo", "Failed to register user", e)
            Result.failure(e)
        }
    }

    suspend fun approveUser(userId: String, adminName: String): Result<Unit> {
        return try {
            usersCollection.document(userId).update(
                mapOf(
                    "status" to SipemoConstants.STATUS_APPROVED,
                    "approvedBy" to adminName,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SipemoRepo", "Failed to approve user $userId", e)
            Result.failure(e)
        }
    }

    suspend fun rejectUser(userId: String, reason: String = "Tidak memenuhi persyaratan"): Result<Unit> {
        return try {
            usersCollection.document(userId).update(
                mapOf(
                    "status" to SipemoConstants.STATUS_REJECTED,
                    "rejectionReason" to reason,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SipemoRepo", "Failed to reject user $userId", e)
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SipemoRepo", "Failed to delete user $userId", e)
            Result.failure(e)
        }
    }

    suspend fun checkUserStatus(emailOrName: String): UserProfile? {
        return try {
            val query = usersCollection.whereEqualTo("email", emailOrName.trim().lowercase()).get().await()
            if (!query.isEmpty) {
                query.documents.first().toObject(UserProfile::class.java)
            } else {
                val queryByName = usersCollection.whereEqualTo("fullName", emailOrName.trim()).get().await()
                if (!queryByName.isEmpty) {
                    queryByName.documents.first().toObject(UserProfile::class.java)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("SipemoRepo", "Error checking user status", e)
            null
        }
    }

    fun observeCars(): Flow<List<CarItem>> {
        return carsCollection.snapshots().map { snapshot ->
            snapshot.documents.mapNotNull { it.toObject(CarItem::class.java) }
        }.catch { e ->
            Log.e("SipemoRepo", "Error observing cars", e)
            emit(emptyList())
        }
    }

    suspend fun seedInitialCarsIfEmpty() {
        try {
            val existing = carsCollection.limit(1).get().await()
            if (existing.isEmpty) {
                val sampleCars = listOf(
                    CarItem(
                        name = "Toyota Avanza 1.3 E",
                        plateNumber = "AG 1234 SK",
                        capacity = 7,
                        transmission = "Manual",
                        status = "AVAILABLE",
                        description = "Mobil operasional umum unit SRT 1 Kabupaten Kediri"
                    ),
                    CarItem(
                        name = "Daihatsu Luxio 1.5 X",
                        plateNumber = "AG 5678 SR",
                        capacity = 8,
                        transmission = "Manual",
                        status = "AVAILABLE",
                        description = "Kapasitas lapang untuk operasional asrama & logistik"
                    ),
                    CarItem(
                        name = "Toyota HiAce Commuter",
                        plateNumber = "AG 9012 RKT",
                        capacity = 15,
                        transmission = "Manual",
                        status = "AVAILABLE",
                        description = "Minibus kunjungan belajar & kegiatan ekstrakurikuler siswa"
                    )
                )
                for (car in sampleCars) {
                    val doc = carsCollection.document()
                    doc.set(
                        mapOf(
                            "name" to car.name,
                            "plateNumber" to car.plateNumber,
                            "capacity" to car.capacity,
                            "transmission" to car.transmission,
                            "status" to car.status,
                            "description" to car.description,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
        } catch (e: Exception) {
            Log.w("SipemoRepo", "Failed to seed cars: ${e.message}")
        }
    }

    fun observeLoans(): Flow<List<LoanRequest>> {
        return loansCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(LoanRequest::class.java) }
            }
            .catch { e ->
                Log.e("SipemoRepo", "Error observing loans", e)
                emit(emptyList())
            }
    }

    suspend fun submitLoanRequest(loan: LoanRequest): Result<String> {
        return try {
            val doc = loansCollection.document()
            val data = mapOf(
                "userId" to loan.userId,
                "userName" to loan.userName,
                "userJabatan" to loan.userJabatan,
                "carId" to loan.carId,
                "carName" to loan.carName,
                "carPlate" to loan.carPlate,
                "purpose" to loan.purpose,
                "destination" to loan.destination,
                "departureDate" to loan.departureDate,
                "returnDate" to loan.returnDate,
                "driverType" to loan.driverType,
                "status" to "PENDING",
                "notes" to loan.notes,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            doc.set(data).await()
            Result.success(doc.id)
        } catch (e: Exception) {
            Log.e("SipemoRepo", "Failed to submit loan", e)
            Result.failure(e)
        }
    }
}
