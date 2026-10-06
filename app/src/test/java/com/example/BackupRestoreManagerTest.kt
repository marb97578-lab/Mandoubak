package com.example

import com.example.util.BackupManager
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.security.MessageDigest

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BackupRestoreManagerTest {

    @Test
    fun testValidBackupValidation() {
        val payloadObj = JSONObject().apply {
            put("formatVersion", 1)
            put("appName", "Mandoubak")
            put("timestamp", 1700000000000L)
            put("backupType", "MANUAL")
            put("settings", JSONObject().apply {
                put("companyName", "مؤسسة التوزيع")
                put("representativeName", "المندوب")
                put("currencySymbol", "ر.س")
            })
            put("products", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("id", 1L)
                    put("name", "عصير برتقال")
                    put("costPrice", 4.0)
                    put("salePrice", 6.0)
                    put("stockQuantity", 50)
                })
            })
            put("clients", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("id", 1L)
                    put("name", "بقالة الأمانة")
                    put("phone", "0501234567")
                    put("currentBalance", 100.0)
                })
            })
        }

        val digest = MessageDigest.getInstance("SHA-256")
        val dataString = payloadObj.toString(2)
        val hash = digest.digest(dataString.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

        val rootObj = JSONObject().apply {
            put("appName", "Mandoubak")
            put("formatVersion", 1)
            put("checksumSha256", hash)
            put("totalRecords", 2)
            put("payload", payloadObj)
        }

        val result = BackupManager.validateBackupContent(rootObj.toString(2))
        assertTrue(result.isValid)
        assertEquals(2, result.totalRecords)
        assertEquals(1, result.productsCount)
        assertEquals(1, result.clientsCount)
        assertNotNull(result.parsedPayload)
        assertEquals("عصير برتقال", result.parsedPayload?.products?.first()?.name)
    }

    @Test
    fun testTamperedBackupValidationFails() {
        val payloadObj = JSONObject().apply {
            put("formatVersion", 1)
            put("appName", "Mandoubak")
            put("timestamp", 1700000000000L)
            put("products", org.json.JSONArray())
            put("clients", org.json.JSONArray())
        }

        // Invalid checksum
        val rootObj = JSONObject().apply {
            put("appName", "Mandoubak")
            put("formatVersion", 1)
            put("checksumSha256", "invalid_tampered_hash_123456789")
            put("totalRecords", 0)
            put("payload", payloadObj)
        }

        val result = BackupManager.validateBackupContent(rootObj.toString(2))
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("البصمة الرقمية") == true)
    }

    @Test
    fun testIncompatibleAppBackupRejected() {
        val rootObj = JSONObject().apply {
            put("appName", "SomeOtherApp")
            put("formatVersion", 1)
            put("checksumSha256", "hash")
            put("payload", JSONObject())
        }

        val result = BackupManager.validateBackupContent(rootObj.toString())
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("غير متوافق") == true)
    }
}
