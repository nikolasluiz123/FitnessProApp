package br.com.fitnesspro.tests.integration.room

import androidx.paging.PagingSource
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.tuple.AcademyTuple
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SaveAcademyTimeUseCaseIntegrationRoomTests {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var academyDAO: AcademyDAO
    
    @Inject
    lateinit var personAcademyTimeDAO: PersonAcademyTimeDAO

    @Inject
    lateinit var personDAO: PersonDAO

    @Inject
    lateinit var userDAO: UserDAO

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun should_return_active_academy_list_when_execute_get(): Unit = runBlocking {
        createSimpleAcademyList()

        val academyList = getAcademies()

        academyList.forEach { tuple ->
            val academy = academyDAO.findAcademyById(tuple.id)

            academy.active shouldBe true
        }
    }

    @Test
    fun should_return_active_academy_list_with_filtered_name_is_last_part_when_execute_get(): Unit = runBlocking {
        createSimpleAcademyList()

        val filter = "2"

        val academyList = getAcademies(simpleFilter = filter)

        academyList shouldHaveSize 1
        academyList.first().name shouldContain filter
    }

    @Test
    fun should_return_active_academy_list_with_filtered_name_is_first_part_when_execute_get(): Unit = runBlocking {
        createSimpleAcademyList()

        val filter = "Academy"
        val academyList = getAcademies(simpleFilter = filter)

        academyList shouldHaveSize 3
        academyList.map { it.name }.forEach { it shouldContain filter }
        academyList.forEach { academyDAO.findAcademyById(it.id).active shouldBe true }
    }

    @Test
    fun should_return_conflict_when_time_start_inner_range_of_other_academy_time_with_same_day_of_week(): Unit = runBlocking {
        createSimpleAcademyTimeList()

        val academyTime = PersonAcademyTime(
            personId = "1",
            academyId = "2",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            timeStart = LocalTime.of(10, 30),
            timeEnd = LocalTime.of(12, 0)
        )

        val conflict = personAcademyTimeDAO.getConflictPersonAcademyTime(
            personId = academyTime.personId!!,
            personAcademyTimeId = academyTime.id,
            dayOfWeek = academyTime.dayOfWeek!!,
            start = academyTime.timeStart!!,
            end = academyTime.timeEnd!!
        )

        conflict.shouldNotBeNull()
        conflict.dayOfWeek shouldBe DayOfWeek.WEDNESDAY
        conflict.timeStart shouldBe LocalTime.of(10, 0)
        conflict.timeEnd shouldBe LocalTime.of(11, 0)
    }

    @Test
    fun should_return_conflict_when_time_end_inner_range_of_other_academy_time_with_same_day_of_week(): Unit = runBlocking {
        createSimpleAcademyTimeList()

        val academyTime = PersonAcademyTime(
            personId = "1",
            academyId = "2",
            dayOfWeek = DayOfWeek.MONDAY,
            timeStart = LocalTime.of(9, 30),
            timeEnd = LocalTime.of(10, 30)
        )

        val conflict = personAcademyTimeDAO.getConflictPersonAcademyTime(
            personId = academyTime.personId!!,
            personAcademyTimeId = academyTime.id,
            dayOfWeek = academyTime.dayOfWeek!!,
            start = academyTime.timeStart!!,
            end = academyTime.timeEnd!!
        )

        conflict.shouldNotBeNull()
        conflict.dayOfWeek shouldBe DayOfWeek.MONDAY
        conflict.timeStart shouldBe LocalTime.of(10, 0)
        conflict.timeEnd shouldBe LocalTime.of(11, 0)
    }

    @Test
    fun should_return_conflict_when_different_day_of_week_and_same_time_range(): Unit = runBlocking {
        createSimpleAcademyTimeList()

        val academyTime = PersonAcademyTime(
            personId = "1",
            academyId = "2",
            dayOfWeek = DayOfWeek.TUESDAY,
            timeStart = LocalTime.of(10, 0),
            timeEnd = LocalTime.of(11, 0)
        )

        val conflict = personAcademyTimeDAO.getConflictPersonAcademyTime(
            personId = academyTime.personId!!,
            personAcademyTimeId = academyTime.id,
            dayOfWeek = academyTime.dayOfWeek!!,
            start = academyTime.timeStart!!,
            end = academyTime.timeEnd!!
        )

        conflict shouldBe null
    }

    @Test
    fun should_return_conflict_when_time_range_is_after(): Unit = runBlocking {
        createSimpleAcademyTimeList()

        val academyTime = PersonAcademyTime(
            personId = "1",
            academyId = "1",
            dayOfWeek = DayOfWeek.MONDAY,
            timeStart = LocalTime.of(13, 0),
            timeEnd = LocalTime.of(14, 0)
        )

        val conflict = personAcademyTimeDAO.getConflictPersonAcademyTime(
            personId = academyTime.personId!!,
            personAcademyTimeId = academyTime.id,
            dayOfWeek = academyTime.dayOfWeek!!,
            start = academyTime.timeStart!!,
            end = academyTime.timeEnd!!
        )

        conflict shouldBe null
    }

    @Test
    fun should_return_conflict_when_time_range_is_before(): Unit = runBlocking {
        createSimpleAcademyTimeList()

        val academyTime = PersonAcademyTime(
            personId = "1",
            academyId = "1",
            dayOfWeek = DayOfWeek.MONDAY,
            timeStart = LocalTime.of(7, 0),
            timeEnd = LocalTime.of(8, 0)
        )

        val conflict = personAcademyTimeDAO.getConflictPersonAcademyTime(
            personId = academyTime.personId!!,
            personAcademyTimeId = academyTime.id,
            dayOfWeek = academyTime.dayOfWeek!!,
            start = academyTime.timeStart!!,
            end = academyTime.timeEnd!!
        )

        conflict shouldBe null
    }

    @Test
    fun should_return_only_active_academies_from_person(): Unit = runBlocking {
        val users = listOf(
            User(
                email = "email@gmail.com",
                password = "passoword",
                type = EnumUserType.PERSONAL_TRAINER
            ),
            User(
                email = "email2@gmail.com",
                password = "passoword2",
                type = EnumUserType.NUTRITIONIST
            )
        )

        val persons = listOf(
            Person(
                id = "1",
                userId = users[0].id,
                name = "Person 1"
            ),
            Person(
                id = "2",
                userId = users[1].id,
                name = "Person 2"
            )
        )

        val academies = listOf(
            Academy(id = "1", name = "Academy 1"),
            Academy(id = "2", name = "Academy 2"),
            Academy(id = "3", name = "Academy 3"),
            Academy(id = "4", name = "Academy 4", active = false)
        )

        userDAO.insertBatch(users)
        personDAO.insertBatch(persons)
        academyDAO.insertBatch(academies)

        val academyTimes = listOf(
            PersonAcademyTime(
                personId = "1",
                academyId = "1",
                dayOfWeek = DayOfWeek.MONDAY,
                timeStart = LocalTime.of(10, 0),
                timeEnd = LocalTime.of(11, 0)
            ),
            PersonAcademyTime(
                personId = "1",
                academyId = "1",
                dayOfWeek = DayOfWeek.WEDNESDAY,
                timeStart = LocalTime.of(10, 0),
                timeEnd = LocalTime.of(11, 0)
            ),
            PersonAcademyTime(
                personId = "1",
                academyId = "1",
                dayOfWeek = DayOfWeek.FRIDAY,
                timeStart = LocalTime.of(10, 0),
                timeEnd = LocalTime.of(11, 0)
            ),
            PersonAcademyTime(
                personId = "2",
                academyId = "1",
                dayOfWeek = DayOfWeek.MONDAY,
                timeStart = LocalTime.of(10, 0),
                timeEnd = LocalTime.of(11, 0)
            ),
            PersonAcademyTime(
                personId = "2",
                academyId = "1",
                dayOfWeek = DayOfWeek.WEDNESDAY,
                timeStart = LocalTime.of(10, 0),
                timeEnd = LocalTime.of(11, 0)
            ),
            PersonAcademyTime(
                personId = "2",
                academyId = "1",
                dayOfWeek = DayOfWeek.FRIDAY,
                timeStart = LocalTime.of(10, 0),
                timeEnd = LocalTime.of(11, 0)
            )
        )

        personAcademyTimeDAO.insertBatch(academyTimes)

        val personId = "1"
        val personAcademies = academyDAO.getAcademiesFromPerson(personId)

        personAcademies shouldHaveSize 1
        personAcademies.first().id shouldBe "1"
        personAcademies.first().active shouldBe true
    }

    private suspend fun createSimpleAcademyTimeList() {
        val user = User(
            email = "email@gmail.com",
            password = "passoword",
            type = EnumUserType.PERSONAL_TRAINER
        )

        val person = Person(
            id = "1",
            userId = user.id,
            name = "Person 1"
        )

        val academyTimes = listOf(
            PersonAcademyTime(
                personId = "1",
                academyId = "1",
                dayOfWeek = DayOfWeek.MONDAY,
                timeStart = LocalTime.of(10, 0),
                timeEnd = LocalTime.of(11, 0)
            ),
            PersonAcademyTime(
                personId = "1",
                academyId = "1",
                dayOfWeek = DayOfWeek.WEDNESDAY,
                timeStart = LocalTime.of(10, 0),
                timeEnd = LocalTime.of(11, 0)
            ),
            PersonAcademyTime(
                personId = "1",
                academyId = "1",
                dayOfWeek = DayOfWeek.FRIDAY,
                timeStart = LocalTime.of(10, 0),
                timeEnd = LocalTime.of(11, 0)
            )
        )

        userDAO.insert(user)
        personDAO.insert(person)
        academyDAO.insertBatch(listOf(Academy(id = "1", name = "Academy 1")))
        personAcademyTimeDAO.insertBatch(academyTimes)
    }

    private suspend fun createSimpleAcademyList() {
        val academies = listOf(
            Academy(name = "Academy 1"),
            Academy(name = "Academy 2"),
            Academy(name = "Academy 3"),
            Academy(name = "Academy 4", active = false)
        )

        academyDAO.insertBatch(academies)
    }

    private suspend fun getAcademies(simpleFilter: String = ""): List<AcademyTuple> {
        val pagingSource = academyDAO.getAcademies(name = simpleFilter)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                null,
                20,
                false
            )
        ) as PagingSource.LoadResult.Page

        return result.data
    }

}