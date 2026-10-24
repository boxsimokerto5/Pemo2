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

enum class AppScreen {
    LOGIN,
    REGISTER,
    PENDING_APPROVAL,
    ADMIN_DASHBOARD,
    USER_DASHBOARD
}

data class SipemoUiState(
    val currentScreen: AppScreen = AppScreen.LOGIN,
    val currentUser: UserProfile? = null,
    val pendingVerificationUser: UserProfile? = null,
    val isAdmin: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val adminSelectedTab: Int = 0,
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

    fun loginAdmin(user: String, pass: String) {
        val trimmedUser = user.trim()
        val trimmedPass = pass.trim()

        if (trimmedUser == SipemoConstants.ADMIN_USERNAME && trimmedPass == SipemoConstants.ADMIN_PASSWORD) {
            _uiState.update {
                it.copy(
                    isAdmin = true,
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
                    successMessage = "Selamat datang, Administrator!"
                )
            }
        } else {
            _uiState.update {
                it.copy(errorMessage = "Kredensial Admin salah! Gunakan user: admin dan password: Woyowoyo12@")
            }
        }
    }

    fun loginUser(identifier: String) {
        if (identifier.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Silakan masukkan nama lengkap atau email terdaftar") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val user = repository.checkUserStatus(identifier)
            _uiState.update { it.copy(isLoading = false) }

            if (user == null) {
                _uiState.update {
                    it.copy(errorMessage = "Akun tidak ditemukan. Silakan lakukan pendaftaran akun terlebih dahulu.")
                }
            } else {
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
                        _uiState.update {
                            it.copy(
                                currentUser = user,
                                isAdmin = false,
                                currentScreen = AppScreen.USER_DASHBOARD,
                                successMessage = "Selamat datang, ${user.fullName}!"
                            )
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

    fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        unitKerja: String,
        jabatan: String
    ) {
        if (fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nama lengkap wajib diisi") }
            return
        }
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email / username wajib diisi") }
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

    fun logout() {
        _uiState.update {
            SipemoUiState(
                currentScreen = AppScreen.LOGIN,
                successMessage = "Anda telah berhasil keluar (Logout)."
            )
        }
    }
}
