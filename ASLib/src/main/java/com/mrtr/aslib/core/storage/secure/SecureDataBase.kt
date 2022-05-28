package com.mrtr.aslib.core.storage.secure

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Storage::class])
internal abstract class SecureDataBase : RoomDatabase() {
    abstract fun secureModelDao() : StorageModelDao

}