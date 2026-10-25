package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SipemoRepository
import com.example.model.CarItem
import com.example.model.LoanRequest
import com.example.model.SipemoConstants
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    LOGIN,
    REGISTER,
    PENDING_APPROVAL,
    ADMIN_DASHBOARD,
    USER_DASHBOARD,
    SECURITY_DASHBOARD
}

data class SipemoUiState(
    val currentScreen: AppScreen = AppScreen.LOGIN,
    val currentUser: UserProfile? = null,
    val pendingVerificationUser: UserProfile? = null,
    val isAdmin: Boolean = false,
    val isSecurity: Boolean = false,
    val securityOfficerName: String = "Petugas Keamanan (Pos Gerbang)",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val adminSelectedTab: Int = 0,
    val securitySelectedTab: Int = 0, // 0: Izin Gerbang, 1: Live Map Pemantauan
    val userFilterStatus: String = "ALL", // ALL, PENDING, APPROVED, REJECTED
    val userFilterJabatan: String = "ALL",
    val searchQuery: String = "",
    val selectedCarForLoan: CarItem? = null,
    val isLoanDialogOpen: Boolean = false
)

class SipemoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SipemoRepository(application)

    private val _uiState = MutableStateFlow(SipemoUiState())
    val uiState: StateFlow<SipemoUiState> = _uiState.asStateFlow()

    val allUsers: StateFlow<List<UserProfile>> = repository.observeAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingUsers: StateFlow<List<UserProfile>> = repository.observePendingUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cars: StateFlow<List<CarItem>> = repository.observeCars()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val loans: StateFlow<List<LoanRequest>> = repository.observeLoans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.seedInitialCarsIfEmpty()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen, errorMessage = null, successMessage = null) }
    }

    /**
     * Unified Login for Users and Admin:
     * Admin can log in using admin / Woyowoyo12@ or registered admin credentials.
     * Regular users log in using their email/username and chosen password.
     */
    fun loginUnified(identifier: String, pass: String) {
        val trimmedUser = identifier.trim()
        val trimmedPass = pass.trim()

        if (trimmedUser.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Silakan masukkan nama lengkap / email terdaftar.") }
            return
        }
        if (trimmedPass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Silakan masukkan kata sandi Anda.") }
            return
        }

        // 1. Direct match with master Admin credentials
        if (trimmedUser.equals(SipemoConstants.ADMIN_USERNAME, ignoreCase = true) &&
            trimmedPass == SipemoConstants.ADMIN_PASSWORD
        ) {
            _uiState.update {
                it.copy(
                    isAdmin = true,
                    isSecurity = false,
                    currentUser = UserProfile(
                        id = "admin_root",
                        fullName = "Administrator Sekolah Rakyat",
                        email = "admin@sekolahrakyat.sch.id",
                        unitKerja = "SRT 1 KABUPATEN KEDIRI",
                        jabatan = "Administrator Sistem",
                        status = SipemoConstants.STATUS_APPROVED,
                        role = SipemoConstants.ROLE_ADMIN
                    ),
                    currentScreen = AppScreen.ADMIN_DASHBOARD,
                    errorMessage = null,
                    successMessage = "Selamat datang, Administrator Sekolah Rakyat!"
                )
            }
            return
        }

        // 2. Database / In-Memory User Check
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val user = repository.checkUserStatus(trimmedUser)
            _uiState.update { it.copy(isLoading = false) }

            if (user == null) {
                _uiState.update {
                    it.copy(errorMessage = "Akun tidak ditemukan. Silakan lakukan pendaftaran akun terlebih dahulu.")
                }
            } else {
                // Check password match (if user has set a password)
                if (user.password.isNotBlank() && user.password != trimmedPass) {
                    _uiState.update {
                        it.copy(errorMessage = "Kata sandi salah! Silakan periksa kembali kata sandi Anda.")
                    }
                    return@launch
                }

                when (user.status) {
                    SipemoConstants.STATUS_PENDING -> {
                        _uiState.update {
                            it.copy(
                                pendingVerificationUser = user,
                                currentScreen = AppScreen.PENDING_APPROVAL
                            )
                        }
                    }
                    SipemoConstants.STATUS_APPROVED -> {
                        if (user.role == SipemoConstants.ROLE_ADMIN) {
                            _uiState.update {
                                it.copy(
                                    currentUser = user,
                                    isAdmin = true,
                                    isSecurity = false,
                                    currentScreen = AppScreen.ADMIN_DASHBOARD,
                                    successMessage = "Selamat datang, ${user.fullName} (Administrator)!"
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    currentUser = user,
                                    isAdmin = false,
                                    isSecurity = false,
                                    currentScreen = AppScreen.USER_DASHBOARD,
                                    successMessage = "Selamat datang, ${user.fullName}!"
                                )
                            }
                        }
                    }
                    SipemoConstants.STATUS_REJECTED -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = "Mohon maaf, permohonan akun Anda ditolak oleh Admin: ${user.rejectionReason.ifBlank { "Tidak memenuhi verifikasi unit kerja" }}"
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Dedicated Security Login (Petugas Keamanan / Pengamanan Dalam)
     */
    fun loginSecurity(username: String, pass: String) {
        val trimmedUser = username.trim()
        val trimmedPass = pass.trim()

        if (trimmedUser.isBlank() || trimmedPass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Username dan Kata Sandi Keamanan wajib diisi.") }
            return
        }

        if (trimmedUser.equals(SipemoConstants.SECURITY_USERNAME, ignoreCase = true) &&
            trimmedPass == SipemoConstants.SECURITY_PASSWORD
        ) {
            _uiState.update {
                it.copy(
                    isSecurity = true,
                    isAdmin = false,
                    currentUser = UserProfile(
                        id = "sec_officer_1",
                        fullName = "Bripka Agus Sulistyo",
                        email = "keamanan@sekolahrakyat.sch.id",
                        unitKerja = "SRT 1 KABUPATEN KEDIRI",
                        jabatan = "Pengamanan Dalam",
                        status = SipemoConstants.STATUS_APPROVED,
                        role = SipemoConstants.ROLE_SECURITY
                    ),
                    securityOfficerName = "Bripka Agus (Pos Gerbang)",
                    currentScreen = AppScreen.SECURITY_DASHBOARD,
                    errorMessage = null,
                    successMessage = "Selamat bertugas, Petugas Keamanan! Siap memantau pergerakan mobil."
                )
            }
        } else {
            _uiState.update {
                it.copy(errorMessage = "Kredensial Keamanan salah! Gunakan user: keamanan dan password: SatpamSRT123@")
            }
        }
    }

    fun loginAdmin(user: String, pass: String) {
        loginUnified(user, pass)
    }

    fun loginUser(identifier: String) {
        loginUnified(identifier, "User123@")
    }

    fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        unitKerja: String,
        jabatan: String,
        password: String = "User123@"
    ) {
        if (fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama lengkap wajib diisi.") }
            return
        }
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email / username wajib diisi.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Kata sandi minimal 6 karakter.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val newUser = UserProfile(
                fullName = fullName.trim(),
                email = email.trim().lowercase(),
                phone = phone.trim(),
                unitKerja = unitKerja,
                jabatan = jabatan,
                password = password.trim(),
                status = SipemoConstants.STATUS_PENDING,
                role = SipemoConstants.ROLE_USER
            )

            val result = repository.registerUser(newUser)
            _uiState.update { it.copy(isLoading = false) }

            if (result.isSuccess) {
                val registeredWithId = newUser.copy(id = result.getOrDefault(""))
                _uiState.update {
                    it.copy(
                        pendingVerificationUser = registeredWithId,
                        currentScreen = AppScreen.PENDING_APPROVAL,
                        successMessage = "Pendaftaran akun berhasil dikirim ke Admin!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Gagal mendaftar: ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    fun approveUser(userId: String, userName: String) {
        viewModelScope.launch {
            val result = repository.approveUser(userId, "Admin (Woyowoyo12@)")
            if (result.isSuccess) {
                _uiState.update { it.copy(successMessage = "Akun '$userName' berhasil disetujui! Pengguna kini dapat login.") }
            } else {
                _uiState.update { it.copy(errorMessage = "Gagal menyetujui akun: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun rejectUser(userId: String, userName: String, reason: String = "Tidak memenuhi syarat verifikasi") {
        viewModelScope.launch {
            val result = repository.rejectUser(userId, reason)
            if (result.isSuccess) {
                _uiState.update { it.copy(successMessage = "Akun '$userName' telah ditolak.") }
            } else {
                _uiState.update { it.copy(errorMessage = "Gagal menolak akun: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
            _uiState.update { it.copy(successMessage = "Data pengguna berhasil dihapus.") }
        }
    }

    fun setAdminTab(tabIndex: Int) {
        _uiState.update { it.copy(adminSelectedTab = tabIndex) }
    }

    fun setSecurityTab(tabIndex: Int) {
        _uiState.update { it.copy(securitySelectedTab = tabIndex) }
    }

    fun setUserFilterStatus(status: String) {
        _uiState.update { it.copy(userFilterStatus = status) }
    }

    fun setUserFilterJabatan(jabatan: String) {
        _uiState.update { it.copy(userFilterJabatan = jabatan) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun openLoanDialog(car: CarItem) {
        _uiState.update { it.copy(selectedCarForLoan = car, isLoanDialogOpen = true) }
    }

    fun closeLoanDialog() {
        _uiState.update { it.copy(selectedCarForLoan = null, isLoanDialogOpen = false) }
    }

    fun submitLoanRequest(
        purpose: String,
        destination: String,
        departureDate: String,
        returnDate: String,
        driverType: String,
        notes: String
    ) {
        val user = _uiState.value.currentUser ?: return
        val car = _uiState.value.selectedCarForLoan ?: return

        if (purpose.isBlank() || destination.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Keperluan dan Tujuan peminjaman wajib diisi") }
            return
        }

        viewModelScope.launch {
            val loan = LoanRequest(
                userId = user.id,
                userName = user.fullName,
                userJabatan = user.jabatan,
                carId = car.id,
                carName = car.name,
                carPlate = car.plateNumber,
                purpose = purpose.trim(),
                destination = destination.trim(),
                departureDate = departureDate.ifBlank { "Hari ini" },
                returnDate = returnDate.ifBlank { "Besok" },
                driverType = driverType,
                notes = notes.trim()
            )

            val result = repository.submitLoanRequest(loan)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoanDialogOpen = false,
                        selectedCarForLoan = null,
                        successMessage = "Pengajuan peminjaman mobil ${car.name} berhasil diajukan!"
                    )
                }
            } else {
                _uiState.update { it.copy(errorMessage = "Gagal mengajukan peminjaman: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    // Security Gate Approval Actions
    fun approveGateDeparture(loanId: String, carName: String) {
        val timeNow = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault()).format(Date())
        val officer = _uiState.value.securityOfficerName
        viewModelScope.launch {
            val res = repository.approveGateDeparture(loanId, officer, timeNow)
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(successMessage = "Izin keluar gerbang untuk mobil $carName telah disetujui pada pukul $timeNow.")
                }
            }
        }
    }

    fun confirmGateReturn(loanId: String, carName: String) {
        val timeNow = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault()).format(Date())
        val officer = _uiState.value.securityOfficerName
        viewModelScope.launch {
            val res = repository.confirmGateReturn(loanId, officer, timeNow)
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(successMessage = "Mobil $carName telah dikonfirmasi kembali dan parkir di gerbang/asrama pada $timeNow.")
                }
            }
        }
    }

    fun logout() {
        _uiState.update {
            SipemoUiState(
                currentScreen = AppScreen.LOGIN,
                successMessage = "Anda telah berhasil keluar (Logout)."
            )
        }
    }
}
