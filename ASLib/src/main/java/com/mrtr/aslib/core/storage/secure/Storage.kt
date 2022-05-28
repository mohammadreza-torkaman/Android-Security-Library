package com.mrtr.aslib.core.storage.secure

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Storage(@PrimaryKey val key: String, val value: String)