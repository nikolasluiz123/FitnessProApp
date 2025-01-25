package br.com.fitnesspro.tests.integration.room

import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.model.scheduler.Scheduler
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.shouldBe
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SaveSchedulerUseCaseIntegrationRoomTests {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var userDAO: UserDAO

    @Inject
    lateinit var personDAO: PersonDAO

    @Inject
    lateinit var schedulerDAO: SchedulerDAO

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun should_return_false_for_conflict_in_personal_schedule_when_default_test_data_used(): Unit = runBlocking {
        createSchedules()

        val conflict = schedulerDAO.getHasSchedulerConflict(
            schedulerId = null,
            personId = "1",
            userType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = LocalDate.of(2025, 1, 20),
            start = LocalTime.of(10, 0),
            end = LocalTime.of(11, 30)
        )

        conflict shouldBe false
    }

    @Test
    fun should_return_false_for_conflict_in_nutritionist_schedule_when_default_test_data_used(): Unit = runBlocking {
        createSchedules()

        val conflict = schedulerDAO.getHasSchedulerConflict(
            schedulerId = null,
            personId = "2",
            userType = EnumUserType.NUTRITIONIST,
            scheduledDate = LocalDate.of(2025, 1, 20),
            start = LocalTime.of(10, 0),
            end = LocalTime.of(11, 30)
        )

        conflict shouldBe false
    }

    @Test
    fun should_return_false_for_conflict_in_academy_member_schedule_when_default_test_data_used(): Unit = runBlocking {
        createSchedules()

        val conflict = schedulerDAO.getHasSchedulerConflict(
            schedulerId = null,
            personId = "3",
            userType = EnumUserType.ACADEMY_MEMBER,
            scheduledDate = LocalDate.of(2025, 1, 20),
            start = LocalTime.of(10, 0),
            end = LocalTime.of(11, 30)
        )

        conflict shouldBe false
    }

    @Test
    fun should_return_true_for_conflict_when_exists_active_schedule_with_same_date_and_time_start_inner_range(): Unit = runBlocking {
        createSchedules()

        val newScheduler = Scheduler(
            id = "6",
            academyMemberPersonId = "3",
            professionalPersonId = "1",
            scheduledDate = LocalDate.of(2025, 1, 19),
            start = LocalTime.of(13, 30),
            end = LocalTime.of(14, 30),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.RECURRENT
        )

        val conflict = schedulerDAO.getHasSchedulerConflict(
            schedulerId = newScheduler.id,
            personId = newScheduler.academyMemberPersonId!!,
            userType = EnumUserType.ACADEMY_MEMBER,
            scheduledDate = newScheduler.scheduledDate!!,
            start = newScheduler.start!!,
            end = newScheduler.end!!
        )

        conflict shouldBe true
    }

    @Test
    fun should_return_true_for_conflict_when_exists_active_schedule_with_same_date_and_time_end_inner_range(): Unit = runBlocking {
        createSchedules()

        val newScheduler = Scheduler(
            id = "6",
            academyMemberPersonId = "3",
            professionalPersonId = "1",
            scheduledDate = LocalDate.of(2025, 1, 19),
            start = LocalTime.of(9, 30),
            end = LocalTime.of(10, 30),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.RECURRENT
        )

        val conflict = schedulerDAO.getHasSchedulerConflict(
            schedulerId = newScheduler.id,
            personId = newScheduler.academyMemberPersonId!!,
            userType = EnumUserType.ACADEMY_MEMBER,
            scheduledDate = newScheduler.scheduledDate!!,
            start = newScheduler.start!!,
            end = newScheduler.end!!
        )

        conflict shouldBe true
    }

    @Test
    fun should_return_false_for_conflict_when_exists_active_schedule_with_other_date(): Unit = runBlocking {
        createSchedules()

        val newScheduler = Scheduler(
            id = "6",
            academyMemberPersonId = "3",
            professionalPersonId = "1",
            scheduledDate = LocalDate.of(2025, 1, 24),
            start = LocalTime.of(9, 30),
            end = LocalTime.of(10, 30),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.RECURRENT
        )

        val conflict = schedulerDAO.getHasSchedulerConflict(
            schedulerId = newScheduler.id,
            personId = newScheduler.academyMemberPersonId!!,
            userType = EnumUserType.ACADEMY_MEMBER,
            scheduledDate = newScheduler.scheduledDate!!,
            start = newScheduler.start!!,
            end = newScheduler.end!!
        )

        conflict shouldBe false
    }

    @Test
    fun should_return_active_schedules_with_specific_date_when_get_with_filters(): Unit = runBlocking {
        createSchedules()

        val schedules = schedulerDAO.getSchedulerList(
            personId = "1",
            userType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = LocalDate.of(2025, 1, 19)
        )

        schedules.map { it.id!! }.shouldContainOnly("2", "5")
    }

    @Test
    fun should_return_active_schedules_for_month_when_get_with_filters(): Unit = runBlocking {
        createSchedules()

        val schedules = schedulerDAO.getSchedulerList(
            personId = "1",
            userType = EnumUserType.PERSONAL_TRAINER,
            yearMonth = YearMonth.of(2025, 2)
        )

        schedules.map { it.id!! }.shouldContainOnly("88", "99")
    }

    private suspend fun createSchedules() {
        val users = listOf(
            User(
                id = "1",
                email = "teste@gmail.com",
                password = "teste123456",
                type = EnumUserType.PERSONAL_TRAINER,
            ),
            User(
                id = "2",
                email = "teste2@gmail.com",
                password = "teste2123456",
                type = EnumUserType.NUTRITIONIST,
            ),
            User(
                id = "3",
                email = "teste3@gmail.com",
                password = "teste3123456",
                type = EnumUserType.ACADEMY_MEMBER,
            ),
        )

        val persons = listOf(
            Person(
                id = "1",
                name = "Personal Trainer",
                userId = users[0].id,
            ),
            Person(
                id = "2",
                name = "Nutritionist",
                userId = users[1].id,
            ),
            Person(
                id = "3",
                name = "Academy Member",
                userId = users[2].id,
            )
        )

        val schedules = listOf(
            Scheduler(
                id = "1",
                academyMemberPersonId = persons[2].id,
                professionalPersonId = persons[0].id,
                scheduledDate = LocalDate.of(2025, 1, 18),
                start = LocalTime.of(10, 0),
                end = LocalTime.of(11, 30),
                situation = EnumSchedulerSituation.CONFIRMED,
                compromiseType = EnumCompromiseType.RECURRENT
            ),
            Scheduler(
                id = "2",
                academyMemberPersonId = persons[2].id,
                professionalPersonId = persons[0].id,
                scheduledDate = LocalDate.of(2025, 1, 19),
                start = LocalTime.of(10, 0),
                end = LocalTime.of(11, 30),
                situation = EnumSchedulerSituation.SCHEDULED,
                compromiseType = EnumCompromiseType.RECURRENT
            ),
            Scheduler(
                id = "3",
                academyMemberPersonId = persons[2].id,
                professionalPersonId = persons[1].id,
                scheduledDate = LocalDate.of(2025, 1, 19),
                start = LocalTime.of(13, 0),
                end = LocalTime.of(14, 30),
                situation = EnumSchedulerSituation.SCHEDULED,
                compromiseType = EnumCompromiseType.RECURRENT
            ),
            Scheduler(
                id = "4",
                academyMemberPersonId = persons[2].id,
                professionalPersonId = persons[1].id,
                scheduledDate = LocalDate.of(2025, 1, 19),
                start = LocalTime.of(13, 0),
                end = LocalTime.of(14, 30),
                situation = EnumSchedulerSituation.SCHEDULED,
                compromiseType = EnumCompromiseType.RECURRENT,
                active = false
            ),
            Scheduler(
                id = "5",
                academyMemberPersonId = persons[2].id,
                professionalPersonId = persons[0].id,
                scheduledDate = LocalDate.of(2025, 1, 19),
                start = LocalTime.of(10, 0),
                end = LocalTime.of(11, 30),
                situation = EnumSchedulerSituation.CANCELLED,
                compromiseType = EnumCompromiseType.RECURRENT
            ),
            Scheduler(
                id = "88",
                academyMemberPersonId = persons[2].id,
                professionalPersonId = persons[0].id,
                scheduledDate = LocalDate.of(2025, 2, 19),
                start = LocalTime.of(10, 0),
                end = LocalTime.of(11, 30),
                situation = EnumSchedulerSituation.SCHEDULED,
                compromiseType = EnumCompromiseType.RECURRENT
            ),
            Scheduler(
                id = "99",
                academyMemberPersonId = persons[2].id,
                professionalPersonId = persons[0].id,
                scheduledDate = LocalDate.of(2025, 2, 20),
                start = LocalTime.of(10, 0),
                end = LocalTime.of(11, 30),
                situation = EnumSchedulerSituation.SCHEDULED,
                compromiseType = EnumCompromiseType.RECURRENT
            ),
        )

        userDAO.saveBatch(users)
        personDAO.saveBatch(persons)
        schedulerDAO.saveBatch(schedules)
    }
}