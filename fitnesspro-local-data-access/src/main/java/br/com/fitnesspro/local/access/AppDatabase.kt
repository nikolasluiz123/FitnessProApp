package br.com.fitnesspro.local.access

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.fitnesspro.model.User

/**
 * Classe que representa a base de dados local do app
 */
@Database(
    version = 1,
    entities = [
        User::class
    ],
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

}