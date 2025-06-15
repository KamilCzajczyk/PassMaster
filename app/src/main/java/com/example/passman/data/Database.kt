package com.example.passman.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


//data class StoredPassword(
//    val id: Long,
//    var name: String,
//    var url: String? = null,
//    var login: String? = null,
//    var password: String,
//    var comment: String? = null,
//    var fav: Boolean = false
//)


@Entity(tableName = "password_entries")
data class PasswordEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serviceName: String,
    val login: String,
    val password : String,
    val isFavorite: Boolean = false
)
