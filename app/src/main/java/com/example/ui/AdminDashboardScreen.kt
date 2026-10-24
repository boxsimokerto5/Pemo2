package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CarItem
import com.example.model.SipemoConstants
import com.example.model.UserProfile
import com.example.ui.theme.SipemoAmber
import com.example.ui.theme.SipemoEmerald
import com.example.ui.theme.SipemoNavyBorder
import com.example.ui.theme.SipemoNavyCard
import com.example.ui.theme.SipemoNavyDark
import com.example.ui.theme.SipemoRose
import com.example.viewmodel.SipemoUiState
import com.example.viewmodel.SipemoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    state: SipemoUiState,
    viewModel: SipemoViewModel,
    modifier: Modifier = Modifier
) {
    val pendingUsersList by viewModel.pendingUsers.collectAsState()
    val allUsersList by viewModel.allUsers.collectAsState()
    val carsList by viewModel.cars.collectAsState()

    var rejectTargetUser by remember { mutableStateOf<UserProfile?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SipemoAmber.copy(alpha = 0.2f))
                                .border(1.dp, SipemoAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = SipemoAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Panel Admin SIPEMO",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "SRT 1 KABUPATEN KEDIRI",
                                fontSize = 11.sp,
                                color = SipemoEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout Admin",
                            tint = Color(0xFFEF4444)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SipemoNavyDark
                )
            )
        },
        containerColor = SipemoNavyDark
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Action Feedback Notification
            state.successMessage?.let { msg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { viewModel.dismissMessage() },
                    shape = RoundedCornerShape(10.dp),
                    color = SipemoEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoEmerald)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = msg, fontSize = 12.sp, color = Color(0xFFA7F3D0), modifier = Modifier.weight(1f))
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color(0xFF6EE7B7), modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Quick Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pending Count
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.setAdminTab(0) },
                    shape = RoundedCornerShape(14.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (pendingUsersList.isNotEmpty()) SipemoAmber else SipemoNavyBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Perlu Persetujuan", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${pendingUsersList.size}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (pendingUsersList.isNotEmpty()) SipemoAmber else Color.White
                            )
                            if (pendingUsersList.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("baru", fontSize = 10.sp, color = SipemoAmber)
                            }
                        }
                    }
                }

                // Total Users
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.setAdminTab(1) },
                    shape = RoundedCornerShape(14.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Pengguna", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${allUsersList.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SipemoEmerald
                        )
                    }
                }

                // Total Cars
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.setAdminTab(2) },
                    shape = RoundedCornerShape(14.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Armada Mobil", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${carsList.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Tab Navigation Row
            TabRow(
                selectedTabIndex = state.adminSelectedTab,
                containerColor = SipemoNavyDark,
                contentColor = SipemoEmerald,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[state.adminSelectedTab]),
                        color = SipemoEmerald
                    )
                }
            ) {
                Tab(
                    selected = state.adminSelectedTab == 0,
                    onClick = { viewModel.setAdminTab(0) },
                    text = {
                        BadgedBox(
                            badge = {
                                if (pendingUsersList.isNotEmpty()) {
                                    Badge(containerColor = SipemoAmber, contentColor = SipemoNavyDark) {
                                        Text("${pendingUsersList.size}", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Text(
                                "Persetujuan",
                                fontWeight = if (state.adminSelectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (state.adminSelectedTab == 0) SipemoEmerald else Color(0xFF94A3B8)
                            )
                        }
                    }
                )
                Tab(
                    selected = state.adminSelectedTab == 1,
                    onClick = { viewModel.setAdminTab(1) },
                    text = {
                        Text(
                            "Daftar User",
                            fontWeight = if (state.adminSelectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (state.adminSelectedTab == 1) SipemoEmerald else Color(0xFF94A3B8)
                        )
                    }
                )
                Tab(
                    selected = state.adminSelectedTab == 2,
                    onClick = { viewModel.setAdminTab(2) },
                    text = {
                        Text(
                            "Armada Mobil",
                            fontWeight = if (state.adminSelectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (state.adminSelectedTab == 2) SipemoEmerald else Color(0xFF94A3B8)
                        )
                    }
                )
            }

            // Tab Content
            when (state.adminSelectedTab) {
                0 -> {
                    // TAB 0: Persetujuan Akun
                    PendingApprovalTab(
                        pendingUsers = pendingUsersList,
                        onApprove = { user -> viewModel.approveUser(user.id, user.fullName) },
                        onReject = { user -> viewModel.rejectUser(user.id, user.fullName) }
                    )
                }
                1 -> {
                    // TAB 1: Daftar Seluruh User
                    UserDirectoryTab(
                        allUsers = allUsersList,
                        filterStatus = state.userFilterStatus,
                        filterJabatan = state.userFilterJabatan,
                        searchQuery = state.searchQuery,
                        onFilterStatusChanged = { viewModel.setUserFilterStatus(it) },
                        onFilterJabatanChanged = { viewModel.setUserFilterJabatan(it) },
                        onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                        onApprove = { user -> viewModel.approveUser(user.id, user.fullName) },
                        onDelete = { user -> viewModel.deleteUser(user.id) }
                    )
                }
                2 -> {
                    // TAB 2: Armada Mobil SIPEMO
                    FleetTab(cars = carsList)
                }
            }
        }
    }
}

@Composable
fun PendingApprovalTab(
    pendingUsers: List<UserProfile>,
    onApprove: (UserProfile) -> Unit,
    onReject: (UserProfile) -> Unit
) {
    if (pendingUsers.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SipemoEmerald.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SipemoEmerald,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Semua Akun Telah Diproses",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Saat ada pengguna baru yang mendaftar di Sekolah Rakyat, permohonan persetujuan akan langsung muncul di sini.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "MENUNGGU PERSETUJUAN (${pendingUsers.size} AKUN)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SipemoAmber,
                    letterSpacing = 1.sp
                )
            }

            items(pendingUsers, key = { it.id }) { user ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .testTag("pending_user_card_${user.id}"),
                    shape = RoundedCornerShape(16.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoAmber.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(SipemoEmerald.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.fullName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SipemoAmber.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = user.jabatan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SipemoAmber,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Details
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SipemoNavyDark.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Unit: ${user.unitKerja}", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                }
                                if (user.email.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Email/ID: ${user.email}", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                }
                                if (user.phone.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Kontak: ${user.phone}", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Approve & Reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onReject(user) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SipemoRose),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoRose),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("reject_button_${user.id}")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tolak", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onApprove(user) },
                                colors = ButtonDefaults.buttonColors(containerColor = SipemoEmerald),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(44.dp)
                                    .testTag("approve_button_${user.id}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = SipemoNavyDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Setujui Akun", color = SipemoNavyDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserDirectoryTab(
    allUsers: List<UserProfile>,
    filterStatus: String,
    filterJabatan: String,
    searchQuery: String,
    onFilterStatusChanged: (String) -> Unit,
    onFilterJabatanChanged: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onApprove: (UserProfile) -> Unit,
    onDelete: (UserProfile) -> Unit
) {
    val filteredList = allUsers.filter { user ->
        val matchesStatus = when (filterStatus) {
            "ALL" -> true
            else -> user.status == filterStatus
        }
        val matchesJabatan = when (filterJabatan) {
            "ALL" -> true
            else -> user.jabatan.equals(filterJabatan, ignoreCase = true)
        }
        val matchesSearch = searchQuery.isBlank() ||
                user.fullName.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true) ||
                user.jabatan.contains(searchQuery, ignoreCase = true)

        matchesStatus && matchesJabatan && matchesSearch
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filter Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Cari nama, email, atau jabatan...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SipemoEmerald) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus", tint = Color(0xFF94A3B8))
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SipemoEmerald,
                    unfocusedBorderColor = SipemoNavyBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "Semua Status",
                    SipemoConstants.STATUS_APPROVED to "Disetujui",
                    SipemoConstants.STATUS_PENDING to "Menunggu",
                    SipemoConstants.STATUS_REJECTED to "Ditolak"
                ).forEach { (code, label) ->
                    val isSelected = filterStatus == code
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onFilterStatusChanged(code) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SipemoEmerald else SipemoNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SipemoEmerald else SipemoNavyBorder)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) SipemoNavyDark else Color(0xFFCBD5E1),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Jabatan Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL" to "Semua Jabatan")
                    .plus(SipemoConstants.JABATAN_LIST.map { it to it })
                    .forEach { (code, label) ->
                        val isSelected = filterJabatan == code
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onFilterJabatanChanged(code) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SipemoAmber else SipemoNavyCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SipemoAmber else SipemoNavyBorder)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SipemoNavyDark else Color(0xFFCBD5E1),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
            }
        }

        // List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "DITEMUKAN ${filteredList.size} PENGGUNA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }

            items(filteredList, key = { it.id }) { user ->
                val statusColor = when (user.status) {
                    SipemoConstants.STATUS_APPROVED -> SipemoEmerald
                    SipemoConstants.STATUS_PENDING -> SipemoAmber
                    else -> SipemoRose
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(statusColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = statusColor, modifier = Modifier.size(22.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = user.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "${user.jabatan} • ${user.unitKerja}", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = statusColor.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = when (user.status) {
                                            SipemoConstants.STATUS_APPROVED -> "AKTIF / DISETUJUI"
                                            SipemoConstants.STATUS_PENDING -> "MENUNGGU PERSETUJUAN"
                                            else -> "DITOLAK"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        if (user.status == SipemoConstants.STATUS_PENDING) {
                            Button(
                                onClick = { onApprove(user) },
                                colors = ButtonDefaults.buttonColors(containerColor = SipemoEmerald),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Setujui", color = SipemoNavyDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            IconButton(onClick = { onDelete(user) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFF64748B))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FleetTab(cars: List<CarItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ARMADA MOBIL DINAS SEKOLAH RAKYAT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SipemoEmerald,
                letterSpacing = 1.sp
            )
        }

        items(cars, key = { it.id }) { car ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SipemoNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SipemoEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = car.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "${car.plateNumber} • ${car.capacity} Penumpang", fontSize = 12.sp, color = SipemoAmber, fontWeight = FontWeight.SemiBold)
                        if (car.description.isNotBlank()) {
                            Text(text = car.description, fontSize = 11.sp, color = Color(0xFF94A3B8), maxLines = 1)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (car.status == "AVAILABLE") SipemoEmerald.copy(alpha = 0.2f) else SipemoRose.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (car.status == "AVAILABLE") "SIAP" else "DIPINJAM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (car.status == "AVAILABLE") SipemoEmerald else SipemoRose,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
