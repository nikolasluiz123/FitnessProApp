package br.com.fitnesspro.tests.ui.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import br.com.fitnesspro.compose.components.fields.enums.EnumDropdownMenuTestTags.DROP_DOWN_MENU_OUTLINED_TEXT_FIELD
import io.kotest.matchers.shouldBe

fun AndroidComposeTestRule<*, *>.assertWriteTextField(value: String, testTag: Enum<*>, assertValue: String = value): SemanticsNodeInteraction {
    writeTextField(testTag, value)
    return onNodeWithTag(testTag.name).assert(hasText(assertValue))
}

fun AndroidComposeTestRule<*, *>.writeTextField(testTag: Enum<*>, value: String) {
    onNodeWithTag(testTag.name).performTextReplacement(value)
}

fun AndroidComposeTestRule<*, *>.assertDisplayed(testTag: Enum<*>): SemanticsNodeInteraction {
    return onNodeWithTag(testTag.name).assertIsDisplayed()
}

fun AndroidComposeTestRule<*, *>.assertNotDisplayed(testTag: Enum<*>): SemanticsNodeInteraction {
    return onNodeWithTag(testTag.name).assertIsNotDisplayed()
}

fun AndroidComposeTestRule<*, *>.onFirst(testTag: Enum<*>): SemanticsNodeInteraction {
    return onAllNodesWithTag(testTag.name).onFirst()
}

fun AndroidComposeTestRule<*, *>.assertWithText(text: String, testTag: Enum<*>): SemanticsNodeInteraction {
    return onNodeWithTag(testTag.name).assert(hasText(text))
}

fun AndroidComposeTestRule<*, *>.assertDropDownMenuWithText(testTag: Enum<*>, text: String): SemanticsNodeInteraction {
    return onTagWithParent(DROP_DOWN_MENU_OUTLINED_TEXT_FIELD, testTag).assert(hasText(text))
}

fun AndroidComposeTestRule<*, *>.onPosition(position: Int, testTag: Enum<*>): SemanticsNodeInteraction {
    return onAllNodesWithTag(testTag.name)[position]
}

fun AndroidComposeTestRule<*, *>.assertPositionWithText(text: String, position: Int, testTag: Enum<*>): SemanticsNodeInteraction {
    return onPosition(position, testTag).assert(hasText(text))
}

fun AndroidComposeTestRule<*, *>.assertDisplayedWithText(text: String): SemanticsNodeInteraction {
    return onNodeWithText(text).assertIsDisplayed()
}

fun AndroidComposeTestRule<*, *>.onClick(testTag: Enum<*>): SemanticsNodeInteraction {
    return onNodeWithTag(testTag.name).performClick()
}

fun AndroidComposeTestRule<*, *>.onClickFirst(testTag: Enum<*>): SemanticsNodeInteraction {
    return onFirst(testTag).performClick()
}

fun AndroidComposeTestRule<*, *>.onClickWithText(text: String): SemanticsNodeInteraction {
    return onNodeWithText(text).performClick()
}

fun AndroidComposeTestRule<*, *>.onTagWithParent(testTag: Enum<*>, parentTestTag: Enum<*>): SemanticsNodeInteraction {
    return onNode(hasTestTag(testTag.name).and(hasParent(hasTestTag(parentTestTag.name))))
}

fun AndroidComposeTestRule<*, *>.onClickWithParent(testTag: Enum<*>, parentTestTag: Enum<*>): SemanticsNodeInteraction {
    return onTagWithParent(testTag, parentTestTag).performClick()
}

fun AndroidComposeTestRule<*, *>.assertRequiredTextFieldValidation(
    fieldTag: Enum<*>,
    buttonTag: Enum<*>,
    message: String
): SemanticsNodeInteraction {
    writeTextField(fieldTag, "")
    onClick(buttonTag)

    waitUntil(5000) {
        onNodeWithText(message).isDisplayed()
    }

    return onNodeWithText(message).assertIsDisplayed()
}

fun AndroidComposeTestRule<*, *>.assertOnlyDigitsFieldValidation(testTag: Enum<*>, value: String): SemanticsNodeInteraction {
    writeTextField(testTag, "")
    return assertWriteTextField(
        value = value,
        testTag = testTag,
        assertValue = ""
    )
}

fun AndroidComposeTestRule<*, *>.assertEnabled(testTag: Enum<*>, enabled: Boolean): SemanticsNodeInteraction {
    return if (enabled) {
        onNodeWithTag(testTag.name).assertIsEnabled()
    } else {
        onNodeWithTag(testTag.name).assertIsNotEnabled()
    }
}

fun AndroidComposeTestRule<*, *>.assertColorWithText(text: String, color: Color): Int {
    val image = onNodeWithText(text).captureToImage()
    val pixel = image.asAndroidBitmap().getPixel(image.width/2, image.height/2)

    return pixel shouldBe color.convert(ColorSpaces.Srgb).hashCode()
}
