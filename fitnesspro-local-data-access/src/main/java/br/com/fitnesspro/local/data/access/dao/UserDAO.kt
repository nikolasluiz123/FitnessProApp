package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.general.User
import java.util.StringJoiner

@Dao
abstract class UserDAO: IntegratedMaintenanceDAO<User>() {

    suspend fun hasUserWithEmail(email: String, userId: String?): Boolean {
        val queryParams = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select 1 ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from user ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where user.active = 1 ")
            add(" and user.email = ?  ")
            queryParams.add(email)

            userId?.let {
                add(" and user.id != ? ")
                queryParams.add(it)
            }
        }

        val sql = StringJoiner(QR_NL).apply {
            add(" select exists ( ")
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(" ) ")
        }

        return executeQueryHasUserWithEmail(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryHasUserWithEmail(query: SupportSQLiteQuery): Boolean

    @Query("select exists (select 1 from user where email = :email and password = :hashedPassword)")
    abstract suspend fun hasUserWithCredentials(email: String, hashedPassword: String): Boolean

    @Query("select * from user where id = :id")
    abstract suspend fun findById(id: String): User?

    @Query("select * from user where email = :email")
    abstract suspend fun findByEmail(email: String): User?

    @Query("""
              select user.* 
              from user
              inner join person on person.user_id = user.id
              where person.id = :personId
    """)
    abstract suspend fun findByPersonId(personId: String): User

    @Query("select exists (select 1 from user where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    @Query("select exists (select 1 from user where email = :email and password = :password)")
    abstract suspend fun hasUserWithEmailAndPassword(email: String, password: String): Boolean

}