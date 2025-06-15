package com.example.passman

import android.content.Context
import android.os.Environment
import com.example.passman.data.AppDatabase
import com.example.passman.security.EncryptionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter

suspend fun exportPasswordsToCSV(context: Context): String? = withContext(Dispatchers.IO) {
    val db = AppDatabase.getDatabase(context)
    val dao = db.passwordEntryDao()
    val passwords = dao.getAll() // suspend fun

    val mek = SecuritySetup.SessionManager.getMEK() ?: return@withContext null

    val fileName = "passwords_export.csv"
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, fileName)

    FileWriter(file).use { writer ->
        writer.append("Service,Login,Password\n")
        for (entry in passwords) {
            val decryptedPassword = EncryptionUtils.decrypt(entry.password, mek)
            writer.append("${entry.serviceName},${entry.login},${decryptedPassword}\n")
        }
    }
    return@withContext file.absolutePath
}