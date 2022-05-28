package com.mrtr.aslib.core.storage.secure

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.mrtr.aslib.core.keystore.KeyProvider
import java.io.Closeable


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class SecureStorage
constructor(private val key: String, context: Context, allowMainThreadExecution: Boolean = false) : Closeable {

    private val keyProvider: KeyProvider = KeyProvider
            .provide(context, context.packageName)
    private val db = SecureDataBaseBuilder.build(context, allowMainThreadExecution)
    private val dao = db.secureModelDao()

    fun setValue(value: String) {
        val encrypted = keyProvider.encrypt(value)
        dao.insertValue(Storage(key, encrypted))
    }

    @Throws(KeyNotFoundException::class)
    fun getValue(): String {
        val storage = dao.findByKey(key) ?: throw KeyNotFoundException("Key ($key) not found!")
        return keyProvider.decrypt(storage.value)
    }

    fun exists(): Boolean {
        val storage = dao.findByKey(key)
        return storage != null
    }


    fun delete() {
        dao.delete(Storage(key, ""))
    }

    override fun close() {
        db.close()
    }

    companion object {
        @JvmStatic
        fun clearAllKeys(context: Context, allowMainThreadExecution: Boolean) {
            val db = SecureDataBaseBuilder
                    .build(context, allowMainThreadExecution)
            db.clearAllTables()
            db.close()
        }
    }

}