package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.tuple.PersonTuple
import java.util.StringJoiner

@Dao
abstract class PersonDAO: BaseDAO() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(person: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveBatch(person: List<Person>)

    @Query("select * from person where id = :id")
    abstract suspend fun findPersonById(id: String): Person

    @Query("select * from person where user_id = :userId")
    abstract suspend fun findPersonByUserId(userId: String): Person

    fun getPersonsWithUserType(types: List<EnumUserType>, simpleFilter: String): PagingSource<Int, PersonTuple> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select person.id as id, ")
            add("        person.name as name, ")
            add("        user.type as userType ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from person ")
            add(" inner join user on user.id = person.user_id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where person.active = 1 ")
            add(" and user.active = 1 ")
            add(" and user.type in ")
            concatElementsForIn(types.map(EnumUserType::name), params)
        }

        if (simpleFilter.isNotEmpty()) {
            where.add(" and person.name like ? ")
            params.add("%$simpleFilter%")
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by person.name ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeQueryPersonsWithUserType(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery(observedEntities = [Person::class])
    abstract fun executeQueryPersonsWithUserType(query: SupportSQLiteQuery): PagingSource<Int, PersonTuple>
}