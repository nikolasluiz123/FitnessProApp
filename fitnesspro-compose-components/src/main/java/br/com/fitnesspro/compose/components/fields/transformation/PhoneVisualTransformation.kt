package br.com.fitnesspro.compose.components.fields.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation: VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val rawText = text.text
        val formattedText = StringBuilder()

        val numbersOnly = rawText.filter { it.isDigit() }

        when {
            numbersOnly.length >= 11 -> {
                formattedText.append(
                    "(${numbersOnly.take(2)}) ${numbersOnly.substring(2, 7)}-${numbersOnly.substring(7)}"
                )
            }
            numbersOnly.length >= 10 -> {
                formattedText.append(
                    "(${numbersOnly.take(2)}) ${numbersOnly.substring(2, 6)}-${numbersOnly.substring(6)}"
                )
            }
            else -> {
                formattedText.append(numbersOnly)
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return formattedText.length.coerceAtMost(offset + formattedText.take(offset).count { it in "()- " })
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset - formattedText.take(offset).count { it in "()- " }
            }
        }

        return TransformedText(AnnotatedString(formattedText.toString()), offsetMapping)
    }
}