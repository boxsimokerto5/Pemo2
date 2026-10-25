package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SipemoConstants
import com.example.viewmodel.AppScreen
import com.example.viewmodel.SipemoViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SIPEMO", appName)
  }

  @Test
  fun `verify SipemoConstants unit kerja and jabatan dropdown requirements`() {
    assertTrue(SipemoConstants.UNIT_KERJA_LIST.contains("SRT 1 KABUPATEN KEDIRI"))
    assertEquals(1, SipemoConstants.UNIT_KERJA_LIST.size)

    val expectedJabatan = listOf("Wali Asuh", "Wali Asrama", "Cleaning Service", "Guru", "Pengamanan Dalam")
    for (j in expectedJabatan) {
      assertTrue("Missing jabatan: $j", SipemoConstants.JABATAN_LIST.contains(j))
    }

    assertEquals("admin", SipemoConstants.ADMIN_USERNAME)
    assertEquals("Woyowoyo12@", SipemoConstants.ADMIN_PASSWORD)
    assertEquals("keamanan", SipemoConstants.SECURITY_USERNAME)
    assertEquals("SatpamSRT123@", SipemoConstants.SECURITY_PASSWORD)
  }

  @Test
  fun `verify SipemoViewModel unified login and security gate operations`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = SipemoViewModel(app)
    assertNotNull(viewModel)
    assertEquals(AppScreen.LOGIN, viewModel.uiState.value.currentScreen)

    // 1. Unified login: Admin logs in from unified login
    viewModel.loginUnified("admin", "Woyowoyo12@")
    assertEquals(AppScreen.ADMIN_DASHBOARD, viewModel.uiState.value.currentScreen)
    assertTrue(viewModel.uiState.value.isAdmin)

    // Logout
    viewModel.logout()
    assertEquals(AppScreen.LOGIN, viewModel.uiState.value.currentScreen)

    // 2. Security Login: Satpam / Keamanan logs in
    viewModel.loginSecurity("keamanan", "SatpamSRT123@")
    assertEquals(AppScreen.SECURITY_DASHBOARD, viewModel.uiState.value.currentScreen)
    assertTrue(viewModel.uiState.value.isSecurity)

    // Security Gate Approval
    viewModel.approveGateDeparture("loan_ready_2", "Daihatsu Luxio 1.5 X")
    assertNotNull(viewModel.uiState.value.successMessage)

    // Switch security tab
    viewModel.setSecurityTab(1)
    assertEquals(1, viewModel.uiState.value.securitySelectedTab)

    // Logout
    viewModel.logout()
    assertEquals(AppScreen.LOGIN, viewModel.uiState.value.currentScreen)
  }
}
