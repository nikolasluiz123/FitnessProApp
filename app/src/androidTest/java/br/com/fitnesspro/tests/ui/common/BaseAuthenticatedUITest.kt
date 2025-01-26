package br.com.fitnesspro.tests.ui.common

import android.util.Log
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeBatchUseCase
import br.com.fitnesspro.common.usecase.login.LoginUseCase
import br.com.fitnesspro.common.usecase.person.SavePersonBatchUseCase
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.model.enums.EnumUserType.ACADEMY_MEMBER
import br.com.fitnesspro.model.enums.EnumUserType.NUTRITIONIST
import br.com.fitnesspro.model.enums.EnumUserType.PERSONAL_TRAINER
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.to.TOUser
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import java.time.DayOfWeek
import java.time.LocalTime

@HiltAndroidTest
abstract class BaseAuthenticatedUITest: BaseUITests() {

    @Inject
    lateinit var savePersonUseCase: SavePersonBatchUseCase

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var savePersonAcademyTimeUseCase: SavePersonAcademyTimeBatchUseCase

    @Inject
    lateinit var academyDAO: AcademyDAO

    protected lateinit var toPersons: List<TOPerson>

    protected suspend fun prepareDatabaseWithPersons() {
        val academies = saveAcademies()
        savePersons()
        saveAcademyTimes(academies)
    }

    private suspend fun saveAcademies(): List<Academy> {
        val academies = listOf(Academy(name = "Academy 1"))
        academyDAO.saveAcademiesBatch(academies)
        return academies
    }

    private suspend fun savePersons() {
        toPersons = listOf(
            TOPerson(
                name = "Personal Trainer",
                toUser = TOUser(
                    email = PERSONAL_EMAIL,
                    password = DEFAULT_PASSWORD,
                    type = PERSONAL_TRAINER
                ),
            ),
            TOPerson(
                name = "Nutritionist",
                toUser = TOUser(
                    email = NUTRITIONIST_EMAIL,
                    password = DEFAULT_PASSWORD,
                    type = NUTRITIONIST
                ),
            ),
            TOPerson(
                name = "Academy Member",
                toUser = TOUser(
                    email = MEMBER_EMAIL,
                    password = DEFAULT_PASSWORD,
                    type = ACADEMY_MEMBER
                )
            )
        )

        val result = savePersonUseCase.executeInclusionBatch(toPersons)

        if (result.isNotEmpty()) {
            Log.e(
                getTag(),
                "prepareUsersToLoginTest Save Person Error: ${result.map { it.validationType }}"
            )
        }
    }

    private suspend fun saveAcademyTimes(academies: List<Academy>) {
        val toAcademy = TOAcademy(
            id = academies[0].id,
            name = academies[0].name
        )

        val times = listOf(
            TOPersonAcademyTime(
                personId = toPersons[0].id!!,
                toAcademy = toAcademy,
                timeStart = LocalTime.of(7, 0),
                timeEnd = LocalTime.of(12, 0),
                dayOfWeek = DayOfWeek.MONDAY
            ),
            TOPersonAcademyTime(
                personId = toPersons[0].id!!,
                toAcademy = toAcademy,
                timeStart = LocalTime.of(13, 0),
                timeEnd = LocalTime.of(17, 0),
                dayOfWeek = DayOfWeek.MONDAY
            ),
            TOPersonAcademyTime(
                personId = toPersons[0].id!!,
                toAcademy = toAcademy,
                timeStart = LocalTime.of(7, 0),
                timeEnd = LocalTime.of(12, 0),
                dayOfWeek = DayOfWeek.TUESDAY
            ),
            TOPersonAcademyTime(
                personId = toPersons[0].id!!,
                toAcademy = toAcademy,
                timeStart = LocalTime.of(13, 0),
                timeEnd = LocalTime.of(17, 0),
                dayOfWeek = DayOfWeek.TUESDAY
            ),
            TOPersonAcademyTime(
                personId = toPersons[1].id!!,
                toAcademy = toAcademy,
                timeStart = LocalTime.of(13, 0),
                timeEnd = LocalTime.of(18, 0),
                dayOfWeek = DayOfWeek.MONDAY
            ),
            TOPersonAcademyTime(
                personId = toPersons[1].id!!,
                toAcademy = toAcademy,
                timeStart = LocalTime.of(13, 0),
                timeEnd = LocalTime.of(18, 0),
                dayOfWeek = DayOfWeek.TUESDAY
            ),
            TOPersonAcademyTime(
                personId = toPersons[2].id!!,
                toAcademy = toAcademy,
                timeStart = LocalTime.of(7, 0),
                timeEnd = LocalTime.of(8, 0),
                dayOfWeek = DayOfWeek.MONDAY
            ),
            TOPersonAcademyTime(
                personId = toPersons[2].id!!,
                toAcademy = toAcademy,
                timeStart = LocalTime.of(7, 0),
                timeEnd = LocalTime.of(8, 0),
                dayOfWeek = DayOfWeek.TUESDAY
            )
        )

        val result = savePersonAcademyTimeUseCase.executeInBatches(times)

        if (result.isNotEmpty()) {
            Log.e(
                getTag(),
                "prepareUsersToLoginTest Save Person Academy Time Error: ${result.map { it.validationType }}"
            )
        }
    }

    protected suspend fun authenticatePersonal() {
        val result = loginUseCase.execute(PERSONAL_EMAIL, DEFAULT_PASSWORD)

        if (result.isNotEmpty()) {
            Log.e(getTag(), "Personal Authentication Error: ${result.map { it.validationType }}")
        }
    }

    protected  suspend fun authenticateNutritionist() {
        val result = loginUseCase.execute(NUTRITIONIST_EMAIL, DEFAULT_PASSWORD)

        if (result.isNotEmpty()) {
            Log.e(getTag(), "Nutritionist Authentication Error: ${result.map { it.validationType }}")
        }
    }

    protected  suspend fun authenticateMember() {
        val result = loginUseCase.execute(MEMBER_EMAIL, DEFAULT_PASSWORD)

        if (result.isNotEmpty()) {
            Log.e(getTag(), "Member Authentication Error: ${result.map { it.validationType }}")
        }
    }

    companion object {
        const val MEMBER_EMAIL = "member@gmail.com"
        const val PERSONAL_EMAIL = "personal@gmail.com"
        const val NUTRITIONIST_EMAIL = "nutritionist@gmail.com"
        const val DEFAULT_PASSWORD = "teste123456"
    }
}