package br.com.fitnesspro.core.keyboard

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

val EmailKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Email,
    imeAction = ImeAction.Next
)

val LastEmailKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Email,
    imeAction = ImeAction.Done
)

val PasswordKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Password,
    imeAction = ImeAction.Next
)

val LastPasswordKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Password,
    imeAction = ImeAction.Done
)

val NormalTextKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Text,
    imeAction = ImeAction.Next
)

val LastNormalTextKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Text,
    imeAction = ImeAction.Done
)


val PhoneKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Phone,
    imeAction = ImeAction.Next
)

val LastPhoneKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Phone,
    imeAction = ImeAction.Done
)

val NumberKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Number,
    imeAction = ImeAction.Next
)

val LastNumberKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Number,
    imeAction = ImeAction.Done
)

val DecimalKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Decimal,
    imeAction = ImeAction.Next
)

val LastDecimalKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Decimal,
    imeAction = ImeAction.Next
)

