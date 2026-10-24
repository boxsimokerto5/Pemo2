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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SipemoAmber
import com.example.ui.theme.SipemoEmerald
import com.example.ui.theme.SipemoNavyBorder
import com.example.ui.theme.SipemoNavyCard
import com.example.ui.theme.SipemoNavyDark
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SipemoUiState
import com.example.viewmodel.SipemoViewModel

@Composable
fun PendingApprovalScreen(
    state: SipemoUiState,
    viewModel: SipemoViewModel,
    modifier: Modifier = Modifier
) {
    val user = state.pendingVerificationUser

    Surface(
        modifier = modifier.fillMaxSize(),
        color = SipemoNavyDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(SipemoAmber.copy(alpha = 0.15f))
                    .border(2.dp, SipemoAmber, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = SipemoAmber,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SipemoAmber.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoAmber)
            ) {
                Text(
                    text = "STATUS: MENUNGGU PERSETUJUAN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SipemoAmber,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pendaftaran Berhasil Dikirim",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Data akun Anda telah tersimpan di database Cloud Firestore Sekolah Rakyat dan sedang menunggu tinjauan dari Administrator.",
                fontSize = 13.sp,
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User Info Card
            if (user != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = SipemoNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Nama Lengkap:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(user.fullName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Unit Kerja:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(user.unitKerja, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SipemoEmerald)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jabatan:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(user.jabatan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SipemoAmber)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Email / ID:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(user.email, fontSize = 12.sp, color = Color(0xFFE2E8F0))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Shortcut to login as admin to test approving right away
            Button(
                onClick = {
                    viewModel.loginAdmin("admin", "Woyowoyo12@")
                },
                colors = ButtonDefaults.buttonColors(containerColor = SipemoAmber),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_approval_shortcut_button")
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SipemoNavyDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buka Halaman Admin (Uji Setujui)", color = SipemoNavyDark, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.navigateTo(AppScreen.LOGIN) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Kembali ke Halaman Login")
            }
        }
    }
}
