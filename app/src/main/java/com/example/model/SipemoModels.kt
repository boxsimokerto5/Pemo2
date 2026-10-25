package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

object SipemoConstants {
    val UNIT_KERJA_LIST = listOf("SRT 1 KABUPATEN KEDIRI")

    val JABATAN_LIST = listOf(
        "Wali Asuh",
        "Wali Asrama",
        "Cleaning Service",
        "Guru",
        "Pengamanan Dalam"
    )

    const val STATUS_PENDING = "PENDING"
    const val STATUS_APPROVED = "APPROVED"
    const val STATUS_REJECTED = "REJECTED"

    const val ROLE_USER = "USER"
    const val ROLE_ADMIN = "ADMIN"
    const val ROLE_SECURITY = "SECURITY"

    const val ADMIN_USERNAME = "admin"
    const val ADMIN_PASSWORD = "Woyowoyo12@"

    const val SECURITY_USERNAME = "keamanan"
    const val SECURITY_PASSWORD = "SatpamSRT123@"
}

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val unitKerja: String = "SRT 1 KABUPATEN KEDIRI",
    val jabatan: String = "Guru",
    val status: String = SipemoConstants.STATUS_PENDING,
    val role: String = SipemoConstants.ROLE_USER,
    val password: String = "User123@",
    val approvedBy: String = "",
    val rejectionReason: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CarItem(
    @DocumentId val id: String = "",
    val name: String = "",
    val plateNumber: String = "",
    val capacity: Int = 7,
    val transmission: String = "Manual",
    val status: String = "AVAILABLE", // AVAILABLE, IN_USE, MAINTENANCE
    val description: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LoanRequest(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userJabatan: String = "",
    val carId: String = "",
    val carName: String = "",
    val carPlate: String = "",
    val purpose: String = "",
    val destination: String = "",
    val departureDate: String = "",
    val returnDate: String = "",
    val driverType: String = "Lepas Kunci", // Lepas Kunci, Dengan Supir
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, RETURNED
    val notes: String = "",
    // Security gate approval and live map tracking
    val securityStatus: String = "MENUNGGU_KELUAR", // MENUNGGU_KELUAR, DI_LUAR, KEMBALI
    val securityOfficer: String = "",
    val gateDepartureTime: String = "",
    val gateReturnTime: String = "",
    val currentLat: Double = -7.8164, // Kediri base
    val currentLng: Double = 112.0119,
    val locationName: String = "Sekolah Rakyat SRT 1 Kediri",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
