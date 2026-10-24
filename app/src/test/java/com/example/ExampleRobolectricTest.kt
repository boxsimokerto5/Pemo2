package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SipemoConstants
import org.junit.Assert.assertEquals
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
  }
}
