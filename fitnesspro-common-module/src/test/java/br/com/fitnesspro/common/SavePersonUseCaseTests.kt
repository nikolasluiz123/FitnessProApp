package br.com.fitnesspro.common

import android.content.Context
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes
import br.com.fitnesspro.common.usecase.person.SavePersonUseCase
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.security.IPasswordHasher
import br.com.fitnesspro.core.validation.getValidations
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import com.github.javafaker.Faker
import io.kotest.matchers.collections.shouldContainOnly
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import java.util.UUID

class SavePersonUseCaseTests {

    private lateinit var context: Context
    private lateinit var faker: Faker
    private lateinit var userRepository: UserRepository
    private lateinit var saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase
    private lateinit var savePersonUseCase: SavePersonUseCase
    private lateinit var passwordHasher: IPasswordHasher

    @BeforeEach
    fun setUp() {
        Locale.setDefault(Locale("pt", "BR"))

        context = mockk(relaxed = true)
        faker = Faker(Locale.getDefault())
        userRepository = mockk(relaxed = true)
        saveSchedulerConfigUseCase = mockk(relaxed = true)
        passwordHasher = mockk(relaxed = true)

        savePersonUseCase = SavePersonUseCase(
            context,
            userRepository,
            saveSchedulerConfigUseCase,
            passwordHasher
        )

        val personSlot = slot<TOPerson>()

        coEvery { userRepository.savePerson(capture(personSlot)) } answers {
            personSlot.captured.id = UUID.randomUUID().toString()
        }

        coEvery { saveSchedulerConfigUseCase.saveConfig(any()) } returns mutableListOf()
    }

    @Test
    fun should_fail_when_email_not_informed(): Unit = runBlocking {
        coEvery { userRepository.hasUserWithEmail(any(), any()) } returns false

        val toPerson = TOPerson(
            name = getFakeName(),
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = getFakePassword(),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResults = savePersonUseCase.execute(toPerson).getValidations()

        validationResults.shouldContainOnly(EnumPersonValidationTypes.REQUIRED_USER_EMAIL)
    }

    @Test
    fun should_fail_when_email_is_greater_than_64_characters(): Unit = runBlocking {
        coEvery { userRepository.hasUserWithEmail(any(), any()) } returns false

        val toPerson = TOPerson(
            name = getFakeName(),
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = getFakePassword(),
                email = "nikolas.silva.martins.2025.projetos.inovadores.exemplares@empresaexemplo.com.br",
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResults = savePersonUseCase.execute(toPerson).getValidations()

        validationResults.shouldContainOnly(EnumPersonValidationTypes.MAX_LENGTH_USER_EMAIL)
    }

    @Test
    fun should_fail_when_email_is_invalid(): Unit = runBlocking {
        coEvery { userRepository.hasUserWithEmail(any(), any()) } returns false

        val toPersonWithEmailWithoutDomainSeparator = TOPerson(
            name = getFakeName(),
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = getFakePassword(),
                email = getFakeEmail().replace("@", ""),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val toPersonWithIncompleteEmail = TOPerson(
            name = getFakeName(),
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = getFakePassword(),
                email = "email.teste@gmail",
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResultsEmailWithoutDomainSeparator = savePersonUseCase.execute(
            toPersonWithEmailWithoutDomainSeparator
        ).getValidations()

        val validationResultsIncompleteEmail = savePersonUseCase.execute(
            toPersonWithIncompleteEmail
        ).getValidations()

        validationResultsEmailWithoutDomainSeparator.shouldContainOnly(EnumPersonValidationTypes.INVALID_USER_EMAIL)
        validationResultsIncompleteEmail.shouldContainOnly(EnumPersonValidationTypes.INVALID_USER_EMAIL)
    }

    @Test
    fun should_fail_when_email_is_in_use(): Unit = runBlocking {
        coEvery { userRepository.hasUserWithEmail(any(), any()) } returns true

        val toPerson = TOPerson(
            name = getFakeName(),
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = getFakePassword(),
                email = "nikolas@gmail.com",
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResults = savePersonUseCase.execute(toPerson).getValidations()

        validationResults.shouldContainOnly(EnumPersonValidationTypes.USER_EMAIL_IN_USE)
    }

    @Test
    fun should_fail_when_password_is_null_or_empty(): Unit = runBlocking {
        coEvery { userRepository.hasUserWithEmail(any(), any()) } returns false

        val toPersonWithNullPassword = TOPerson(
            name = getFakeName(),
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = null,
                email = getFakeEmail(),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val toPersonWithEmptyPassword = TOPerson(
            name = getFakeName(),
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = "  ",
                email = getFakeEmail(),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResultsWithNullPassword = savePersonUseCase.execute(
            toPersonWithNullPassword
        ).getValidations()

        val validationResultsWithEmptyPassword = savePersonUseCase.execute(
            toPersonWithEmptyPassword
        ).getValidations()

        validationResultsWithNullPassword.shouldContainOnly(EnumPersonValidationTypes.REQUIRED_USER_PASSWORD)
        validationResultsWithEmptyPassword.shouldContainOnly(EnumPersonValidationTypes.REQUIRED_USER_PASSWORD)
    }

    @Test
    fun should_fail_when_password_is_greater_than_1024_characters(): Unit = runBlocking {
        val password = "aB3dE5fG7hI9jK1lM2nO3pQ4rS5tU6vW7xY8zA9bC0dE1fG2hI3jK4lM5nO6pQ7rS8tU9vW0xY1zA2bC3dE4fG5hI6jK7lM8nO9pQ0rS1tU2vW3xY4zA5bC6dE7fG8hI9jK0lM1nO2pQ3rS4tU5vW6xY7zA8bC9dE0fG1hI2jK3lM4nO5pQ6rS7tU8vW9xY0zA1bC2dE3fG4hI5jK6lM7nO8pQ9rS0tU1vW2xY3zA4bC5dE6fG7hI8jK9lM0nO1pQ2rS3tU4vW5xY6zA7bC8dE9fG0hI1jK2lM3nO4pQ5rS6tU7vW8xY9zA0bC1dE2fG3hI4jK5lM6nO7pQ8rS9tU0vW1xY2zA3bC4dE5fG6hI7jK8lM9nO0pQ1rS2tU3vW4xY5zA6bC7dE8fG9hI0jK1lM2nO3pQ4rS5tU6vW7xY8zasdasdasdasdfasdfasdfasdfasdqwerituyhoqweiurtyhgweryuitghweoyurihwehjrgweruighweouirhfweouiyrfhweruyifghweyuroighfweruiyfghoweryuifgwerufgweryugwherjghwerouighwerughweyurgohweuryghweuyrghyuwerghuweorhgwyuerghoweryugwherugweorfweyurfgewrgfweyrugfeywrugferyufgwheryufowgerhfuywegweryufgererggyergyoregyuerfgywerugyrfwegyferwgyergyurwefgyuoregyfergyufwergyuferwgyuwergyugyuergyuergyuergyuerwgyuewrgyugeryuwgyurewgyuergyfuregyuferwguyerfwgyuferwgyuerfwgyuerfwgyuerfwgyurefgwyuergyufgyruefwgyurewfgyuerfwgyurefwgyurefwgyurefgyurefwgyurwefgyuerfwgyerfwgyurefgywurgyefuwyguerwfgyuerfwweriu8ewitowerotwioeruitwieuiruiewuhfrweuhirewfhuirefhui"

        coEvery { userRepository.hasUserWithEmail(any(), any()) } returns false

        val toPerson = TOPerson(
            name = getFakeName(),
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = password,
                email = getFakeEmail(),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResults = savePersonUseCase.execute(toPerson).getValidations()

        validationResults.shouldContainOnly(EnumPersonValidationTypes.MAX_LENGTH_USER_PASSWORD)
    }

    @Test
    fun should_fail_when_name_not_informed(): Unit = runBlocking {
        val toPerson = TOPerson(
            name = null,
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = getFakePassword(),
                email = getFakeEmail(),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResults = savePersonUseCase.execute(toPerson).getValidations()

        validationResults.shouldContainOnly(EnumPersonValidationTypes.REQUIRED_PERSON_NAME)
    }

    @Test
    fun should_fail_when_name_is_empty(): Unit = runBlocking {
        val toPerson = TOPerson(
            name = "  ",
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = getFakePassword(),
                email = getFakeEmail(),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResults = savePersonUseCase.execute(toPerson).getValidations()

        validationResults.shouldContainOnly(EnumPersonValidationTypes.REQUIRED_PERSON_NAME)
    }

    @Test
    fun should_fail_when_name_is_greater_than_512_characters(): Unit = runBlocking {
        val name = "a".repeat(513)

        val toPerson = TOPerson(
            name = name,
            birthDate = getFakeBirthDate(),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = getFakePassword(),
                email = getFakeEmail(),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResults = savePersonUseCase.execute(toPerson).getValidations()

        validationResults.shouldContainOnly(EnumPersonValidationTypes.MAX_LENGTH_PERSON_NAME)
    }

    @Test
    fun should_fail_when_birthDate_is_in_the_future(): Unit = runBlocking {
        val toPerson = TOPerson(
            name = getFakeName(),
            birthDate = dateNow().plusDays(1),
            phone = getFakePhoneNumber(),
            toUser = TOUser(
                password = getFakePassword(),
                email = getFakeEmail(),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResults = savePersonUseCase.execute(toPerson).getValidations()

        validationResults.shouldContainOnly(EnumPersonValidationTypes.PERSON_BIRTH_DATE_FUTURE)
    }

    @Test
    fun should_fail_when_phone_is_greater_than_11_characters(): Unit = runBlocking {
        val toPerson = TOPerson(
            name = getFakeName(),
            birthDate = getFakeBirthDate(),
            phone = "123456789012",
            toUser = TOUser(
                password = getFakePassword(),
                email = getFakeEmail(),
                type = EnumUserType.PERSONAL_TRAINER
            )
        )

        val validationResults = savePersonUseCase.execute(toPerson).getValidations()

        validationResults.shouldContainOnly(EnumPersonValidationTypes.MAX_LENGTH_PERSON_PHONE)
    }

    private fun getFakeEmail(): String {
        return faker.internet().emailAddress()
    }

    private fun getFakePassword(): String? {
        return faker.internet().password()
    }

    private fun getFakeBirthDate(): LocalDate? {
        return faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    private fun getFakeName(): String? {
        return faker.name().fullName()
    }

    private fun getFakePhoneNumber(): String {
        return faker.phoneNumber().phoneNumber()
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace(" ", "")
    }
}