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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SipemoConstants
import com.example.ui.theme.SipemoEmerald
import com.example.ui.theme.SipemoEmeraldDark
import com.example.ui.theme.SipemoNavyBorder
import com.example.ui.theme.SipemoNavyCard
import com.example.ui.theme.SipemoNavyDark
import com.example.ui.theme.SipemoRose
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SipemoUiState
import com.example.viewmodel.SipemoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: SipemoUiState,
    viewModel: SipemoViewModel,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var localValidationMsg by remember { mutableStateOf<String?>(null) }

    // Dropdown states
    var isUnitKerjaExpanded by remember { mutableStateOf(false) }
    var selectedUnitKerja by remember { mutableStateOf(SipemoConstants.UNIT_KERJA_LIST.first()) }

    var isJabatanExpanded by remember { mutableStateOf(false) }
    var selectedJabatan by remember { mutableStateOf(SipemoConstants.JABATAN_LIST[3]) } // Default "Guru"

    Surface(
        modifier = modifier.fillMaxSize(),
        color = SipemoNavyDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Back Button & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.LOGIN) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SipemoNavyCard)
                        .border(1.dp, SipemoNavyBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Login",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Pendaftaran Akun Baru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Civitas Sekolah Rakyat SRT 1 Kediri",
                        fontSize = 12.sp,
                        color = SipemoEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error Banner (State error or local validation)
            val displayedError = state.errorMessage ?: localValidationMsg
            displayedError?.let { err ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = SipemoRose.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SipemoRose)
                ) {
                    Text(
                        text = err,
                        color = Color(0xFFFCA5A5),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SipemoNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SipemoEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HowToReg,
                            contentDescription = null,
                            tint = SipemoEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Akun yang Anda daftarkan akan diverifikasi dan disetujui oleh Administrator sebelum dapat digunakan.",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Form Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = SipemoNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SipemoNavyBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // 1. Nama Lengkap
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            localValidationMsg = null
                        },
                        label = { Text("Nama Lengkap *") },
                        placeholder = { Text("Contoh: Budi Santoso, S.Pd.") },
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
                            .testTag("register_fullname_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Dropdown Unit Kerja (Fixed: SRT 1 KABUPATEN KEDIRI)
                    ExposedDropdownMenuBox(
                        expanded = isUnitKerjaExpanded,
                        onExpandedChange = { isUnitKerjaExpanded = !isUnitKerjaExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedUnitKerja,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit Kerja") },
                            leadingIcon = {
                                Icon(Icons.Default.Business, contentDescription = null, tint = SipemoEmerald)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isUnitKerjaExpanded) },
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
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("unit_kerja_dropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = isUnitKerjaExpanded,
                            onDismissRequest = { isUnitKerjaExpanded = false },
                            modifier = Modifier.background(SipemoNavyCard)
                        ) {
                            SipemoConstants.UNIT_KERJA_LIST.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit, color = Color.White) },
                                    onClick = {
                                        selectedUnitKerja = unit
                                        isUnitKerjaExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Dropdown Jabatan
                    ExposedDropdownMenuBox(
                        expanded = isJabatanExpanded,
                        onExpandedChange = { isJabatanExpanded = !isJabatanExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedJabatan,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jabatan *") },
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = SipemoEmerald)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isJabatanExpanded) },
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
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("jabatan_dropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = isJabatanExpanded,
                            onDismissRequest = { isJabatanExpanded = false },
                            modifier = Modifier.background(SipemoNavyCard)
                        ) {
                            SipemoConstants.JABATAN_LIST.forEach { jab ->
                                DropdownMenuItem(
                                    text = { Text(jab, color = Color.White) },
                                    onClick = {
                                        selectedJabatan = jab
                                        isJabatanExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. Email / Username
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            localValidationMsg = null
                        },
                        label = { Text("Email / Username *") },
                        placeholder = { Text("budi@sekolahrakyat.sch.id") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = SipemoEmerald)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                            .testTag("register_email_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5. No. Telepon / WhatsApp
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Nomor WhatsApp (Opsional)") },
                        placeholder = { Text("08123456789") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = SipemoEmerald)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                            .testTag("register_phone_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 6. Kata Sandi (Password)
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            localValidationMsg = null
                        },
                        label = { Text("Kata Sandi (Password) *") },
                        placeholder = { Text("Minimal 6 karakter") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SipemoEmerald)
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
                            focusedBorderColor = SipemoEmerald,
                            unfocusedBorderColor = SipemoNavyBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = SipemoEmerald
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_password_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7. Konfirmasi Kata Sandi
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            localValidationMsg = null
                        },
                        label = { Text("Ulangi Kata Sandi *") },
                        placeholder = { Text("Masukkan kata sandi yang sama") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SipemoEmerald)
                        },
                        trailingIcon = {
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                            .testTag("register_confirm_password_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (password.length < 6) {
                                localValidationMsg = "Kata sandi minimal 6 karakter."
                                return@Button
                            }
                            if (password != confirmPassword) {
                                localValidationMsg = "Konfirmasi kata sandi tidak cocok!"
                                return@Button
                            }
                            viewModel.registerUser(
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                unitKerja = selectedUnitKerja,
                                jabatan = selectedJabatan,
                                password = password
                            )
                        },
                        enabled = !state.isLoading && fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SipemoEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_registration_button")
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = SipemoNavyDark)
                        } else {
                            Text("Daftar Akun Sekarang", color = SipemoNavyDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sudah pernah mendaftar? ", fontSize = 13.sp, color = Color(0xFF94A3B8))
                TextButton(onClick = { viewModel.navigateTo(AppScreen.LOGIN) }) {
                    Text("Masuk", color = SipemoEmerald, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
