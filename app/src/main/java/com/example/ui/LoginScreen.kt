package com.example.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.SipemoConstants
import com.example.ui.theme.SipemoAmber
import com.example.ui.theme.SipemoEmerald
import com.example.ui.theme.SipemoEmeraldDark
import com.example.ui.theme.SipemoNavyBorder
import com.example.ui.theme.SipemoNavyCard
import com.example.ui.theme.SipemoNavyDark
import com.example.ui.theme.SipemoRose
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SipemoUiState
import com.example.viewmodel.SipemoViewModel

@Composable
fun LoginScreen(
    state: SipemoUiState,
    viewModel: SipemoViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: User, 1: Admin

    // User Form
    var userIdentifier by remember { mutableStateOf("") }

    // Admin Form
    var adminUser by remember { mutableStateOf("admin") }
    var adminPass by remember { mutableStateOf("Woyowoyo12@") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = SipemoNavyDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // App Emblem & Branding
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(SipemoEmerald, SipemoEmeraldDark)
                        )
                    )
                    .border(2.dp, SipemoAmber.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "SIPEMO Logo",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SIPEMO",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "Sistem Peminjaman Mobil • Sekolah Rakyat",
                fontSize = 13.sp,
                color = SipemoEmerald,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Unit Kerja: SRT 1 KABUPATEN KEDIRI",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Error or Success Banner
            state.errorMessage?.let { errorMsg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = SipemoRose.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoRose)
                ) {
                    Text(
                        text = errorMsg,
                        color = Color(0xFFFCA5A5),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            state.successMessage?.let { successMsg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = SipemoEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoEmerald)
                ) {
                    Text(
                        text = successMsg,
                        color = Color(0xFFA7F3D0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Tab Switcher (Masuk Pengguna vs Masuk Admin)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = SipemoNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SipemoNavyCard,
                        contentColor = SipemoEmerald,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = SipemoEmerald
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    "Masuk Pengguna",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 0) SipemoEmerald else Color(0xFF94A3B8)
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (selectedTab == 1) SipemoAmber else Color(0xFF94A3B8)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Masuk Admin",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = if (selectedTab == 1) SipemoAmber else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedTab == 0) {
                        // --- FORM USER ---
                        Text(
                            text = "Login Pengguna Terdaftar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Masukkan Nama Lengkap atau Email yang telah disetujui",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = userIdentifier,
                            onValueChange = { userIdentifier = it },
                            label = { Text("Nama Lengkap / Email") },
                            placeholder = { Text("Contoh: Ahmad Santoso") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = SipemoEmerald)
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SipemoEmerald,
                                unfocusedBorderColor = SipemoNavyBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = SipemoEmerald
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_user_input")
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.loginUser(userIdentifier) },
                            enabled = !state.isLoading && userIdentifier.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = SipemoEmerald),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_user_button")
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = SipemoNavyDark)
                            } else {
                                Text("Masuk ke SIPEMO", color = SipemoNavyDark, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Belum punya akun? ",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                "Daftar Akun Baru",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SipemoEmerald,
                                modifier = Modifier
                                    .clickable { viewModel.navigateTo(AppScreen.REGISTER) }
                                    .testTag("nav_to_register_button")
                            )
                        }

                    } else {
                        // --- FORM ADMIN ---
                        Text(
                            text = "Login Administrator Sekolah Rakyat",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Akses panel persetujuan akun & manajemen peminjaman",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = adminUser,
                            onValueChange = { adminUser = it },
                            label = { Text("Username Admin") },
                            leadingIcon = {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SipemoAmber)
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SipemoAmber,
                                unfocusedBorderColor = SipemoNavyBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = SipemoAmber
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_username_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = adminPass,
                            onValueChange = { adminPass = it },
                            label = { Text("Password Admin") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = SipemoAmber)
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SipemoAmber,
                                unfocusedBorderColor = SipemoNavyBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = SipemoAmber
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_password_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick info / autofill hint
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SipemoAmber.copy(alpha = 0.1f))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Kredensial Sesuai Permintaan: User = admin | Pass = Woyowoyo12@",
                                fontSize = 11.sp,
                                color = SipemoAmber,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.loginAdmin(adminUser, adminPass) },
                            colors = ButtonDefaults.buttonColors(containerColor = SipemoAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_admin_button")
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = SipemoNavyDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Masuk Panel Admin", color = SipemoNavyDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Text(
                text = "Pemerintah Kabupaten Kediri • Sekolah Rakyat",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
