package com.mrtr.aslib

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mrtr.aslib.core.storage.secure.KeyNotFoundException
import com.mrtr.aslib.core.storage.secure.SecureStorage
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecureStorageTest {

    private lateinit var secureStorage: SecureStorage

    companion object {
        private const val KEY = "SAMPLE_KEY"
    }

    private lateinit var context: Context

    @Before
    public fun initialize() {
        context = InstrumentationRegistry.getInstrumentation().context
        secureStorage = SecureStorage(KEY, context)
    }

    @Test
    public fun saveValue() {
        val value = "THIS IS A VALUE"
        secureStorage.setValue(value)
        val savedValue = secureStorage.getValue()
        assertEquals(savedValue, value)
    }

    @Test
    public fun checkExistsValue() {
        val value = "THIS IS A VALUE"
        secureStorage.setValue(value)
        val exists = secureStorage.exists()
        assertTrue(exists)
    }

    @Test
    public fun getNotExistsKey() {
        SecureStorage("KEY", context).use {
            try {
                it.getValue()
                fail()
            } catch (e: KeyNotFoundException) {
            }
        }
    }

    @Test
    public fun deleteExistsKey() {
        val value = "THIS IS A VALUE"
        secureStorage.setValue(value)
        secureStorage.delete()
        try {
            val savedValue = secureStorage.getValue()
            assertNull(savedValue)
        } catch (e: KeyNotFoundException) {

        }
    }

    @Test
    public fun deleteNotExistsKey() {
        SecureStorage("KEY2", context).use {
            it.delete()
        }
    }


    @After
    public fun release() {

        SecureStorage.clearAllKeys(context, true)
        secureStorage.close()
    }

}