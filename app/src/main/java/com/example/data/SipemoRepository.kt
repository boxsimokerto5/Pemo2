package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.CarItem
import com.example.model.LoanRequest
import com.example.model.SipemoConstants
import com.example.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SipemoRepository(private val db: FirebaseFirestore? = null) {

    constructor(context: Context) : this(resolveFirestore(context))

    companion object {
        private fun resolveFirestore(context: Context): FirebaseFirestore? {
            return try {
                if (FirebaseApp.getApps(context).isNotEmpty()) {
                    val dbId = try {
                        context.applicationContext.getString(R.string.firestore_database_id)
                    } catch (_: Exception) {
                        ""
                    }
                    if (dbId.isNotBlank()) {
                        FirebaseFirestore.getInstance(dbId)
                    } else {
                        FirebaseFirestore.getInstance()
                    }
                } else {
                    Log.i("SipemoRepo", "FirebaseApp is not initialized; using robust in-memory data store.")
                    null
                }
            } catch (t: Throwable) {
                Log.w("SipemoRepo", "Firestore not available, falling back to in-memory store: ${t.message}")
                null
            }
        }
    }

    private val usersCollection = db?.collection("users")
    private val carsCollection = db?.collection("cars")
    private val loansCollection = db?.collection("loans")

    // In-memory fallback datasets for reliable offline / prototype execution
    private val _inMemoryUsers = MutableStateFlow<List<UserProfile>>(
        listOf(
            UserProfile(
                id = "user_demo_1",
                fullName = "Budi Santoso",
                email = "budi@sekolahrakyat.sch.id",
                phone = "081234567890",
                unitKerja = "SRT 1 KABUPATEN KEDIRI",
                jabatan = "Guru",
                status = SipemoConstants.STATUS_APPROVED,
                role = SipemoConstants.ROLE_USER,
                password = "User123@"
            ),
            UserProfile(
                id = "user_demo_2",
                fullName = "Siti Rahma",
                email = "siti@sekolahrakyat.sch.id",
                phone = "082198765432",
                unitKerja = "SRT 1 KABUPATEN KEDIRI",
                jabatan = "Wali Asuh",
                status = SipemoConstants.STATUS_PENDING,
                role = SipemoConstants.ROLE_USER,
                password = "User123@"
            )
        )
    )

    private val _inMemoryCars = MutableStateFlow<List<CarItem>>(
        listOf(
            CarItem(
                id = "car_1",
                name = "Toyota Avanza 1.3 E",
                plateNumber = "AG 1234 SK",
                capacity = 7,
                transmission = "Manual",
                status = "IN_USE",
                description = "Mobil operasional umum unit SRT 1 Kabupaten Kediri"
            ),
            CarItem(
                id = "car_2",
                name = "Daihatsu Luxio 1.5 X",
                plateNumber = "AG 5678 SR",
                capacity = 8,
                transmission = "Manual",
                status = "AVAILABLE",
                description = "Kapasitas lapang untuk operasional asrama & logistik"
            ),
            CarItem(
                id = "car_3",
                name = "Toyota HiAce Commuter",
                plateNumber = "AG 9012 RKT",
                capacity = 15,
                transmission = "Manual",
                status = "AVAILABLE",
                description = "Minibus kunjungan belajar & kegiatan ekstrakurikuler siswa"
            )
        )
    )

    private val _inMemoryLoans = MutableStateFlow<List<LoanRequest>>(
        listOf(
            LoanRequest(
                id = "loan_active_1",
                userId = "user_demo_1",
                userName = "Budi Santoso",
                userJabatan = "Guru",
                carId = "car_1",
                carName = "Toyota Avanza 1.3 E",
                carPlate = "AG 1234 SK",
                purpose = "Pengantaran Berkas Ujian & Koordinasi Dinas",
                destination = "Kantor Dinas Pendidikan Jawa Timur, Surabaya",
                departureDate = "Hari Ini, 07:30 WIB",
                returnDate = "Hari Ini, 18:00 WIB",
                driverType = "Dengan Supir",
                status = "APPROVED",
                securityStatus = "DI_LUAR",
                securityOfficer = "Bripka Agus (Pos Gerbang)",
                gateDepartureTime = "07:35 WIB",
                currentLat = -7.5360,
                currentLng = 112.2384, // Jombang - tol arah Surabaya
                locationName = "Tol Jombang - Mojokerto KM 682",
                notes = "Membawa 3 guru dan dokumen penting."
            ),
            LoanRequest(
                id = "loan_ready_2",
                userId = "user_demo_1",
                userName = "Budi Santoso",
                userJabatan = "Guru",
                carId = "car_2",
                carName = "Daihatsu Luxio 1.5 X",
                carPlate = "AG 5678 SR",
                purpose = "Belanja Kebutuhan Logistik Asrama Siswa",
                destination = "Pasar Grosir & Supermarket Kediri",
                departureDate = "Hari Ini, 13:00 WIB",
                returnDate = "Hari Ini, 16:30 WIB",
                driverType = "Lepas Kunci",
                status = "APPROVED",
                securityStatus = "MENUNGGU_KELUAR",
                securityOfficer = "",
                gateDepartureTime = "",
                currentLat = -7.8164,
                currentLng = 112.0119,
                locationName = "Pos Gerbang Utama SRT 1 Kediri",
                notes = "Siap verifikasi surat jalan di pos satpam."
            )
        )
    )

    fun observeAllUsers(): Flow<List<UserProfile>> {
        val col = usersCollection
        return if (col != null) {
            col.orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    val docs = snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java) }
                    if (docs.isNotEmpty()) {
                        _inMemoryUsers.value = docs
                    }
                    docs
                }
                .catch { e ->
                    Log.e("SipemoRepo", "Error observing users from Firestore; using in-memory state", e)
                    emit(_inMemoryUsers.value)
                }
        } else {
            _inMemoryUsers.asStateFlow()
        }
    }

    fun observePendingUsers(): Flow<List<UserProfile>> {
        val col = usersCollection
        return if (col != null) {
            col.whereEqualTo("status", SipemoConstants.STATUS_PENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java) }
                }
                .catch { e ->
                    Log.e("SipemoRepo", "Error observing pending users from Firestore; using in-memory state", e)
                    emit(_inMemoryUsers.value.filter { it.status == SipemoConstants.STATUS_PENDING })
                }
        } else {
            _inMemoryUsers.map { list -> list.filter { it.status == SipemoConstants.STATUS_PENDING } }
        }
    }

    suspend fun registerUser(user: UserProfile): Result<String> {
        val docId = if (user.id.isNotBlank()) user.id else "user_${UUID.randomUUID().toString().take(8)}"
        val userWithId = user.copy(id = docId, status = SipemoConstants.STATUS_PENDING)

        // Always update in-memory state
        _inMemoryUsers.update { current ->
            current.filterNot { it.id == docId || it.email.equals(user.email, ignoreCase = true) } + userWithId
        }

        val col = usersCollection
        if (col != null) {
            try {
                val data = mapOf(
                    "fullName" to user.fullName,
                    "email" to user.email,
                    "phone" to user.phone,
                    "unitKerja" to user.unitKerja,
                    "jabatan" to user.jabatan,
                    "password" to user.password,
                    "status" to SipemoConstants.STATUS_PENDING,
                    "role" to SipemoConstants.ROLE_USER,
                    "approvedBy" to "",
                    "rejectionReason" to "",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                col.document(docId).set(data).await()
            } catch (e: Exception) {
                Log.w("SipemoRepo", "Could not write to Firestore; persisted in memory: ${e.message}")
            }
        }

        return Result.success(docId)
    }

    suspend fun approveUser(userId: String, adminName: String): Result<Unit> {
        _inMemoryUsers.update { list ->
            list.map {
                if (it.id == userId) it.copy(status = SipemoConstants.STATUS_APPROVED, approvedBy = adminName) else it
            }
        }

        val col = usersCollection
        if (col != null) {
            try {
                col.document(userId).update(
                    mapOf(
                        "status" to SipemoConstants.STATUS_APPROVED,
                        "approvedBy" to adminName,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                Log.w("SipemoRepo", "Failed to update Firestore on approve: ${e.message}")
            }
        }

        return Result.success(Unit)
    }

    suspend fun rejectUser(userId: String, reason: String = "Tidak memenuhi persyaratan"): Result<Unit> {
        _inMemoryUsers.update { list ->
            list.map {
                if (it.id == userId) it.copy(status = SipemoConstants.STATUS_REJECTED, rejectionReason = reason) else it
            }
        }

        val col = usersCollection
        if (col != null) {
            try {
                col.document(userId).update(
                    mapOf(
                        "status" to SipemoConstants.STATUS_REJECTED,
                        "rejectionReason" to reason,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                Log.w("SipemoRepo", "Failed to update Firestore on reject: ${e.message}")
            }
        }

        return Result.success(Unit)
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        _inMemoryUsers.update { list ->
            list.filterNot { it.id == userId }
        }

        val col = usersCollection
        if (col != null) {
            try {
                col.document(userId).delete().await()
            } catch (e: Exception) {
                Log.w("SipemoRepo", "Failed to delete user in Firestore: ${e.message}")
            }
        }

        return Result.success(Unit)
    }

    suspend fun checkUserStatus(emailOrName: String): UserProfile? {
        val trimmed = emailOrName.trim()
        val col = usersCollection
        if (col != null) {
            try {
                val query = col.whereEqualTo("email", trimmed.lowercase()).get().await()
                if (!query.isEmpty) {
                    return query.documents.first().toObject(UserProfile::class.java)
                }
                val queryByName = col.whereEqualTo("fullName", trimmed).get().await()
                if (!queryByName.isEmpty) {
                    return queryByName.documents.first().toObject(UserProfile::class.java)
                }
            } catch (e: Exception) {
                Log.w("SipemoRepo", "Firestore query failed, searching in-memory: ${e.message}")
            }
        }

        return _inMemoryUsers.value.find {
            it.email.equals(trimmed, ignoreCase = true) || it.fullName.equals(trimmed, ignoreCase = true)
        }
    }

    fun observeCars(): Flow<List<CarItem>> {
        val col = carsCollection
        return if (col != null) {
            col.snapshots().map { snapshot ->
                val docs = snapshot.documents.mapNotNull { it.toObject(CarItem::class.java) }
                if (docs.isNotEmpty()) {
                    _inMemoryCars.value = docs
                }
                docs
            }.catch { e ->
                Log.e("SipemoRepo", "Error observing cars from Firestore; using in-memory state", e)
                emit(_inMemoryCars.value)
            }
        } else {
            _inMemoryCars.asStateFlow()
        }
    }

    suspend fun seedInitialCarsIfEmpty() {
        val col = carsCollection ?: return
        try {
            val existing = col.limit(1).get().await()
            if (existing.isEmpty) {
                for (car in _inMemoryCars.value) {
                    val doc = col.document()
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
            Log.w("SipemoRepo", "Failed to seed cars in Firestore: ${e.message}")
        }
    }

    fun observeLoans(): Flow<List<LoanRequest>> {
        val col = loansCollection
        return if (col != null) {
            col.orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    val docs = snapshot.documents.mapNotNull { it.toObject(LoanRequest::class.java) }
                    if (docs.isNotEmpty()) {
                        _inMemoryLoans.value = docs
                    }
                    docs
                }
                .catch { e ->
                    Log.e("SipemoRepo", "Error observing loans from Firestore; using in-memory state", e)
                    emit(_inMemoryLoans.value)
                }
        } else {
            _inMemoryLoans.asStateFlow()
        }
    }

    suspend fun submitLoanRequest(loan: LoanRequest): Result<String> {
        val docId = if (loan.id.isNotBlank()) loan.id else "loan_${UUID.randomUUID().toString().take(8)}"
        val loanWithId = loan.copy(
            id = docId,
            status = "PENDING",
            securityStatus = "MENUNGGU_KELUAR",
            locationName = "Sekolah Rakyat SRT 1 Kediri"
        )

        _inMemoryLoans.update { current ->
            listOf(loanWithId) + current
        }

        val col = loansCollection
        if (col != null) {
            try {
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
                    "securityStatus" to "MENUNGGU_KELUAR",
                    "locationName" to "Sekolah Rakyat SRT 1 Kediri",
                    "notes" to loan.notes,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                col.document(docId).set(data).await()
            } catch (e: Exception) {
                Log.w("SipemoRepo", "Failed to write loan to Firestore; kept in memory: ${e.message}")
            }
        }

        return Result.success(docId)
    }

    // Security Gate Approval: Approves vehicle departure through gate
    suspend fun approveGateDeparture(loanId: String, officerName: String, departureTime: String): Result<Unit> {
        var carIdToUpdate: String? = null

        _inMemoryLoans.update { list ->
            list.map { loan ->
                if (loan.id == loanId) {
                    carIdToUpdate = loan.carId
                    loan.copy(
                        securityStatus = "DI_LUAR",
                        securityOfficer = officerName,
                        gateDepartureTime = departureTime,
                        locationName = "Sedang Perjalanan Menuju: ${loan.destination}"
                    )
                } else loan
            }
        }

        carIdToUpdate?.let { carId ->
            _inMemoryCars.update { list ->
                list.map { car ->
                    if (car.id == carId) car.copy(status = "IN_USE") else car
                }
            }
        }

        val col = loansCollection
        if (col != null) {
            try {
                col.document(loanId).update(
                    mapOf(
                        "securityStatus" to "DI_LUAR",
                        "securityOfficer" to officerName,
                        "gateDepartureTime" to departureTime,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                Log.w("SipemoRepo", "Failed to update security departure in Firestore: ${e.message}")
            }
        }

        return Result.success(Unit)
    }

    // Security Gate Confirmation: Vehicle returned to Sekolah Rakyat gate
    suspend fun confirmGateReturn(loanId: String, officerName: String, returnTime: String): Result<Unit> {
        var carIdToUpdate: String? = null

        _inMemoryLoans.update { list ->
            list.map { loan ->
                if (loan.id == loanId) {
                    carIdToUpdate = loan.carId
                    loan.copy(
                        securityStatus = "KEMBALI",
                        status = "RETURNED",
                        gateReturnTime = returnTime,
                        securityOfficer = officerName,
                        locationName = "Telah Kembali di Asrama SRT 1 Kediri"
                    )
                } else loan
            }
        }

        carIdToUpdate?.let { carId ->
            _inMemoryCars.update { list ->
                list.map { car ->
                    if (car.id == carId) car.copy(status = "AVAILABLE") else car
                }
            }
        }

        val col = loansCollection
        if (col != null) {
            try {
                col.document(loanId).update(
                    mapOf(
                        "securityStatus" to "KEMBALI",
                        "status" to "RETURNED",
                        "gateReturnTime" to returnTime,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                Log.w("SipemoRepo", "Failed to update security return in Firestore: ${e.message}")
            }
        }

        return Result.success(Unit)
    }
}
