package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.example.model.CarItem
import com.example.ui.theme.SipemoAmber
import com.example.ui.theme.SipemoEmerald
import com.example.ui.theme.SipemoNavyBorder
import com.example.ui.theme.SipemoNavyCard
import com.example.ui.theme.SipemoNavyDark
import com.example.viewmodel.SipemoUiState
import com.example.viewmodel.SipemoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    state: SipemoUiState,
    viewModel: SipemoViewModel,
    modifier: Modifier = Modifier
) {
    val user = state.currentUser
    val carsList by viewModel.cars.collectAsState()
    val loansList by viewModel.loans.collectAsState()

    val myLoans = loansList.filter { it.userId == user?.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SipemoEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("SIPEMO", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Sekolah Rakyat • ${user?.unitKerja ?: "SRT 1 KEDIRI"}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFF94A3B8))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SipemoNavyDark)
            )
        },
        containerColor = SipemoNavyDark
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoEmerald.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(SipemoEmerald.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = user?.fullName ?: "Pengguna SIPEMO",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SipemoAmber.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = user?.jabatan ?: "Guru",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SipemoAmber,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SipemoEmerald.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "TERVERIFIKASI",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SipemoEmerald,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Unit Kerja: ${user?.unitKerja ?: "SRT 1 KABUPATEN KEDIRI"}",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            // Available Cars Section Header
            item {
                Text(
                    text = "ARMADA MOBIL SIAP PINJAM",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SipemoEmerald,
                    letterSpacing = 1.sp
                )
            }

            // Cars items
            items(carsList, key = { it.id }) { car ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SipemoEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SipemoEmerald, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(car.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("${car.plateNumber} • ${car.capacity} Kursi • ${car.transmission}", fontSize = 12.sp, color = SipemoAmber)
                            }
                        }

                        if (car.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(car.description, fontSize = 12.sp, color = Color(0xFF94A3B8))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.openLoanDialog(car) },
                            colors = ButtonDefaults.buttonColors(containerColor = SipemoEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("borrow_car_${car.id}")
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = SipemoNavyDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ajukan Peminjaman Mobil Ini", color = SipemoNavyDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Loan History Section Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "RIWAYAT PENGAJUAN SAYA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            if (myLoans.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = SipemoNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
                    ) {
                        Text(
                            text = "Belum ada riwayat pengajuan peminjaman mobil.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(myLoans, key = { it.id }) { loan ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = SipemoNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(loan.carName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SipemoAmber.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = loan.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SipemoAmber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tujuan: ${loan.destination}", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                            Text("Keperluan: ${loan.purpose}", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("Jadwal: ${loan.departureDate} s/d ${loan.returnDate}", fontSize = 11.sp, color = SipemoEmerald)
                        }
                    }
                }
            }
        }

        // Loan Form Dialog
        if (state.isLoanDialogOpen && state.selectedCarForLoan != null) {
            LoanDialog(
                car = state.selectedCarForLoan,
                onSubmit = { purpose, dest, dep, ret, driver, notes ->
                    viewModel.submitLoanRequest(purpose, dest, dep, ret, driver, notes)
                },
                onDismiss = { viewModel.closeLoanDialog() }
            )
        }
    }
}

@Composable
fun LoanDialog(
    car: CarItem,
    onSubmit: (purpose: String, destination: String, departure: String, returns: String, driver: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var purpose by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var departureDate by remember { mutableStateOf("") }
    var returnDate by remember { mutableStateOf("") }
    var driverType by remember { mutableStateOf("Lepas Kunci") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SipemoNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SipemoEmerald)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Formulir Peminjaman Mobil", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("${car.name} (${car.plateNumber})", fontSize = 13.sp, color = SipemoEmerald, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Keperluan Peminjaman *") },
                    placeholder = { Text("Contoh: Antar jemput siswa asrama / Dinas") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SipemoEmerald,
                        unfocusedBorderColor = SipemoNavyBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Kota / Lokasi Tujuan *") },
                    placeholder = { Text("Contoh: Kantor Pemkab Kediri") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SipemoEmerald,
                        unfocusedBorderColor = SipemoNavyBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = departureDate,
                        onValueChange = { departureDate = it },
                        label = { Text("Tgl Berangkat") },
                        placeholder = { Text("24 Okt, 08:00") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SipemoEmerald,
                            unfocusedBorderColor = SipemoNavyBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = returnDate,
                        onValueChange = { returnDate = it },
                        label = { Text("Tgl Kembali") },
                        placeholder = { Text("24 Okt, 16:00") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SipemoEmerald,
                            unfocusedBorderColor = SipemoNavyBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal", color = Color(0xFF94A3B8))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(purpose, destination, departureDate, returnDate, driverType, notes) },
                        enabled = purpose.isNotBlank() && destination.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SipemoEmerald)
                    ) {
                        Text("Kirim Pengajuan", color = SipemoNavyDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
