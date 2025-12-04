package com.example.sababukia_tbc.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "full_name")
    val fullName: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "activation_status")
    val activationStatus: Int,
    @ColumnInfo(name = "last_active_description")
    val lastActiveDescription: String,
    @ColumnInfo(name = "last_active_epoch")
    val lastActiveEpoch: Long,
    @ColumnInfo(name = "profile_image_url")
    val profileImageUrl: String?
)
