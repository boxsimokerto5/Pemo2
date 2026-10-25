package com.example.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LoanRequest
import com.example.ui.theme.SipemoAmber
import com.example.ui.theme.SipemoEmerald
import com.example.ui.theme.SipemoEmeraldDark
import com.example.ui.theme.SipemoNavyBorder
import com.example.ui.theme.SipemoNavyCard
import com.example.ui.theme.SipemoNavyDark
import com.example.ui.theme.SipemoRose
import com.example.viewmodel.SipemoUiState
import com.example.viewmodel.SipemoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityDashboardScreen(
    state: SipemoUiState,
    viewModel: SipemoViewModel,
    modifier: Modifier = Modifier
) {
    val loans by viewModel.loans.collectAsState()
    var selectedLoanForTracking by remember { mutableStateOf<LoanRequest?>(null) }

    // Group loans by security state
    val waitingDeparture = loans.filter { it.status == "APPROVED" && (it.securityStatus == "MENUNGGU_KELUAR" || it.securityStatus.isBlank()) }
    val currentlyOutside = loans.filter { it.securityStatus == "DI_LUAR" }
    val returnedLoans = loans.filter { it.securityStatus == "KEMBALI" || it.status == "RETURNED" }

    // If no tracking selection yet, pick currently active or waiting
    val activeTrackingLoan = selectedLoanForTracking ?: currentlyOutside.firstOrNull() ?: waitingDeparture.firstOrNull()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SipemoNavyDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SipemoAmber),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = SipemoNavyDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "POS KEAMANAN & GERBANG",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = state.securityOfficerName,
                                fontSize = 11.sp,
                                color = SipemoAmber
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("security_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Keluar",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SipemoNavyCard
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Notification Messages
            state.errorMessage?.let { errorMsg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = SipemoRose.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoRose)
                ) {
                    Text(
                        text = errorMsg,
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            state.successMessage?.let { successMsg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = SipemoEmerald.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoEmerald)
                ) {
                    Text(
                        text = successMsg,
                        color = Color(0xFFA7F3D0),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Tabs: 0 = Izin Keluar Gerbang, 1 = Live Map Pemantauan
            TabRow(
                selectedTabIndex = state.securitySelectedTab,
                containerColor = SipemoNavyCard,
                contentColor = SipemoAmber,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[state.securitySelectedTab]),
                        color = SipemoAmber
                    )
                }
            ) {
                Tab(
                    selected = state.securitySelectedTab == 0,
                    onClick = { viewModel.setSecurityTab(0) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (state.securitySelectedTab == 0) SipemoAmber else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Izin Gerbang",
                                fontWeight = if (state.securitySelectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (state.securitySelectedTab == 0) SipemoAmber else Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                            if (waitingDeparture.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(SipemoRose)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "${waitingDeparture.size}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )

                Tab(
                    selected = state.securitySelectedTab == 1,
                    onClick = { viewModel.setSecurityTab(1) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (state.securitySelectedTab == 1) SipemoEmerald else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Live Map",
                                fontWeight = if (state.securitySelectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (state.securitySelectedTab == 1) SipemoEmerald else Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                            if (currentlyOutside.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(SipemoEmerald)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "${currentlyOutside.size} aktif",
                                        color = SipemoNavyDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )
            }

            if (state.securitySelectedTab == 0) {
                // TAB 0: Persetujuan Izin Gerbang (Gate Clearance)
                GateClearanceTabContent(
                    waitingDeparture = waitingDeparture,
                    currentlyOutside = currentlyOutside,
                    returnedLoans = returnedLoans,
                    onApproveDeparture = { loan ->
                        viewModel.approveGateDeparture(loan.id, loan.carName)
                    },
                    onConfirmReturn = { loan ->
                        viewModel.confirmGateReturn(loan.id, loan.carName)
                    },
                    onViewOnMap = { loan ->
                        selectedLoanForTracking = loan
                        viewModel.setSecurityTab(1)
                    }
                )
            } else {
                // TAB 1: Pemantauan Perjalanan Mobil Melalui Live Map
                LiveMapTrackingTabContent(
                    loans = loans,
                    currentlyOutside = currentlyOutside,
                    selectedLoan = activeTrackingLoan,
                    onSelectLoan = { selectedLoanForTracking = it }
                )
            }
        }
    }
}

@Composable
fun GateClearanceTabContent(
    waitingDeparture: List<LoanRequest>,
    currentlyOutside: List<LoanRequest>,
    returnedLoans: List<LoanRequest>,
    onApproveDeparture: (LoanRequest) -> Unit,
    onConfirmReturn: (LoanRequest) -> Unit,
    onViewOnMap: (LoanRequest) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GateSummaryBadge(
                    title = "Siap Keluar",
                    count = waitingDeparture.size,
                    color = SipemoAmber,
                    modifier = Modifier.weight(1f)
                )
                GateSummaryBadge(
                    title = "Di Luar",
                    count = currentlyOutside.size,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f)
                )
                GateSummaryBadge(
                    title = "Selesai",
                    count = returnedLoans.size,
                    color = SipemoEmerald,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Menunggu Izin Keluar Gerbang
        item {
            Text(
                text = "MENUNGGU PERSETUJUAN KELUAR GERBANG (${waitingDeparture.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SipemoAmber,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (waitingDeparture.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
                ) {
                    Text(
                        text = "Tidak ada mobil yang sedang menunggu izin keluar saat ini.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(waitingDeparture) { loan ->
                GateClearanceCard(
                    loan = loan,
                    onApproveDeparture = onApproveDeparture,
                    onConfirmReturn = onConfirmReturn,
                    onViewOnMap = onViewOnMap
                )
            }
        }

        // Section: Sedang Beroperasi di Luar Sekolah Rakyat
        item {
            Text(
                text = "KENDARAAN SEDANG DI LUAR AREA SEKOLAH (${currentlyOutside.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        if (currentlyOutside.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
                ) {
                    Text(
                        text = "Semua mobil operasional saat ini berada di dalam garasi/asrama.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(currentlyOutside) { loan ->
                GateClearanceCard(
                    loan = loan,
                    onApproveDeparture = onApproveDeparture,
                    onConfirmReturn = onConfirmReturn,
                    onViewOnMap = onViewOnMap
                )
            }
        }

        // Section: Riwayat Kembali
        if (returnedLoans.isNotEmpty()) {
            item {
                Text(
                    text = "RIWAYAT KENDARAAN KEMBALI (${returnedLoans.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            items(returnedLoans) { loan ->
                GateClearanceCard(
                    loan = loan,
                    onApproveDeparture = onApproveDeparture,
                    onConfirmReturn = onConfirmReturn,
                    onViewOnMap = onViewOnMap
                )
            }
        }
    }
}

@Composable
fun GateSummaryBadge(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = SipemoNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun GateClearanceCard(
    loan: LoanRequest,
    onApproveDeparture: (LoanRequest) -> Unit,
    onConfirmReturn: (LoanRequest) -> Unit,
    onViewOnMap: (LoanRequest) -> Unit
) {
    val isOutside = loan.securityStatus == "DI_LUAR"
    val isReturned = loan.securityStatus == "KEMBALI" || loan.status == "RETURNED"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SipemoNavyCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOutside) Color(0xFF38BDF8).copy(alpha = 0.5f) else if (isReturned) SipemoEmerald.copy(alpha = 0.3f) else SipemoAmber.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Car name & status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = loan.carName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Plat: ${loan.carPlate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SipemoAmber
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isOutside) Color(0xFF0369A1).copy(alpha = 0.3f) else if (isReturned) SipemoEmerald.copy(alpha = 0.2f) else SipemoAmber.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isOutside) Color(0xFF38BDF8) else if (isReturned) SipemoEmerald else SipemoAmber
                    )
                ) {
                    Text(
                        text = if (isOutside) "SEDANG DI LUAR" else if (isReturned) "SUDAH KEMBALI" else "SIAP KELUAR",
                        color = if (isOutside) Color(0xFF38BDF8) else if (isReturned) SipemoEmerald else SipemoAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${loan.userName} (${loan.userJabatan}) • Driver: ${loan.driverType}",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tujuan: ${loan.destination}",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOutside && loan.gateDepartureTime.isNotBlank()) "Waktu Keluar: ${loan.gateDepartureTime}" else "Jadwal: ${loan.departureDate}",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            if (isOutside) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Lokasi: ${loan.locationName}",
                        fontSize = 11.sp,
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isOutside && !isReturned) {
                    Button(
                        onClick = { onApproveDeparture(loan) },
                        colors = ButtonDefaults.buttonColors(containerColor = SipemoEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_approve_departure_${loan.id}")
                    ) {
                        Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = SipemoNavyDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Setujui Keluar Gerbang", color = SipemoNavyDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else if (isOutside) {
                    Button(
                        onClick = { onConfirmReturn(loan) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_confirm_return_${loan.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Konfirmasi Masuk Gerbang", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // Button to jump directly to Live Map for this vehicle
                Button(
                    onClick = { onViewOnMap(loan) },
                    colors = ButtonDefaults.buttonColors(containerColor = SipemoNavyDark),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pantau Map", color = SipemoEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Tab 2: Pemantauan Perjalanan Mobil Melalui Live Map Interaktif
 */
@Composable
fun LiveMapTrackingTabContent(
    loans: List<LoanRequest>,
    currentlyOutside: List<LoanRequest>,
    selectedLoan: LoanRequest?,
    onSelectLoan: (LoanRequest) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Real Interactive OpenStreetMap & Satellite Live Map
        SecurityMapView(
            loans = loans,
            selectedLoan = selectedLoan,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Vehicle Inspector Card
        if (selectedLoan != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SipemoNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoEmerald.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SipemoEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${selectedLoan.carName} • ${selectedLoan.carPlate}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Pengemudi / Pemohon: ${selectedLoan.userName}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedLoan.securityStatus == "DI_LUAR") Color(0xFF0369A1).copy(alpha = 0.3f) else SipemoAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (selectedLoan.securityStatus == "DI_LUAR") "SEDANG DI LUAR" else "STANDBY GERBANG",
                                color = if (selectedLoan.securityStatus == "DI_LUAR") Color(0xFF38BDF8) else SipemoAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PinDrop, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Posisi Terkini: ${selectedLoan.locationName}",
                            fontSize = 12.sp,
                            color = SipemoEmerald,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Destinasi: ${selectedLoan.destination}",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }
    }
}

