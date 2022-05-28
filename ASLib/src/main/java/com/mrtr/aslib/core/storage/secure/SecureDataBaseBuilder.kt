package com.mrtr.aslib.core.storage.secure

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.Closeable

internal object SecureDataBaseBuilder {

    fun build(context: Context, allowMainThreadExecution: Boolean = false): SecureDataBase {

        return if (allowMainThreadExecution)
            Room
                    .databaseBuilder(context, SecureDataBase::class.java, "secure_db")
                    .allowMainThreadQueries()
                    .build()
        else
            Room
                    .databaseBuilder(context, SecureDataBase::class.java, "secure_db")
                    .build()
    }
}