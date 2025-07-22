package br.com.fitnesspro.local.data.access.backup

import android.content.Context
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.local.data.access.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.ZoneId

class DatabaseBackupExporter(private val context: Context) {

    suspend fun export(): String = withContext(Dispatchers.IO) {
        val dbFile = context.getDatabasePath(FITNESS_PRO_DB_FILE)
        verifyDatabaseFileExists(dbFile)

        val backupFile = createNewBackupFile()
        writeDatabaseInBackupFile(dbFile, backupFile)

        backupFile.absolutePath
    }

    private fun writeDatabaseInBackupFile(dbFile: File, backupFile: File) {
        Files.copy(dbFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    private fun createNewBackupFile(): File {
        val backupDir = getBackupFileDir()
        val timestamp = dateTimeNow(ZoneId.systemDefault()).format(EnumDateTimePatterns.BACKUP_DB_FILE_NAME)

        return File(backupDir, "$timestamp.db")
    }

    private fun getBackupFileDir(): File {
        return File(context.getExternalFilesDir(null), BACKUPS_DIR_NAME).apply {
            if (!exists()) mkdirs()
        }
    }

    private fun verifyDatabaseFileExists(dbFile: File) {
        if (!dbFile.exists()) {
            val message = context.getString(
                R.string.database_backup_exporter_file_not_found_message,
                dbFile.absolutePath
            )

            throw IllegalStateException(message)
        }
    }

    companion object {
        const val FITNESS_PRO_DB_FILE = "fitnesspro.db"

        private const val BACKUPS_DIR_NAME = "backups"
    }
}