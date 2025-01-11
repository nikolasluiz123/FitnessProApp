package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.tuple.PersonTuple
import java.util.StringJoiner

@Dao
abstract class PersonDAO: BaseDAO() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun savePerson(person: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun savePersons(person: List<Person>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveUsers(user: List<User>)

    @Transaction
    open suspend fun save(user: User, person: Person) {
        saveUser(user)
        savePerson(person)
    }

    @Transaction
    open suspend fun saveBatch(users: List<User>, persons: List<Person>) {
        saveUsers(users)
        savePersons(persons)
    }

    @Query("select * from person where id = :id")
    abstract suspend fun findPersonById(id: String): Person

    @Query("select * from person where user_id = :userId")
    abstract suspend fun findPersonByUserId(userId: String): Person

    @Query("select * from person_academy_time where id = :id")
    abstract suspend fun findPersonAcademyTimeById(id: String): PersonAcademyTime

    @Query("""
        select academy.* 
        from academy
        where academy.active = 1
        and exists (
            select 1
            from person_academy_time pat
            where pat.person_id = :personId
            and pat.academy_id = academy.id
        )
    """)
    abstract suspend fun getAcademies(personId: String): List<Academy>

    @Query("""
        select pat.*
        from person_academy_time pat
        where pat.active = 1
        and pat.person_id = :personId
        and pat.academy_id = :academyId
    """)
    abstract suspend fun getAcademyTimes(personId: String, academyId: String): List<PersonAcademyTime>

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