package com.mrtr.aslib.core.storage.secure

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
internal interface StorageModelDao {

    @Query("Select * from Storage where `key`=:k")
    fun findByKey(k: String): Storage?

    @Insert(onConflict = REPLACE)
    fun insertValue(storage: Storage)

    @Delete()
    fun delete(storage: Storage)
}