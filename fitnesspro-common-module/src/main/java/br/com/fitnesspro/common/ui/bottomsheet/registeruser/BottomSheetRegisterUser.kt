package br.com.fitnesspro.common.ui.bottomsheet.registeruser

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.enums.EnumBottomSheetsTestTags.BOTTOM_SHEET_REGISTER_USER
import br.com.fitnesspro.common.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.compose.components.bottomsheet.BottomSheet
import br.com.fitnesspro.core.R.drawable
import br.com.fitnesspro.firebase.api.analytics.logBottomSheetItemClick
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

/**
 * BottomSheet para registrar um usuário possibilitando a escolha
 * de um dos tipos possíveis.
 *
 * @param onDismissRequest Callback para fechar o BottomSheet
 * @param onItemClickListener Callback para navegar para a tela de cadastro
 */
@Composable
fun BottomSheetRegisterUser(
    onDismissRequest: () -> Unit,
    onItemClickListener: OnNavigateToRegisterUser?
) {
    val items = listOf(
        BottomSheetRegisterUserItem(
            option = EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER,
            labelResId = R.string.label_member_bottom_sheet_register_user,
            iconResId = drawable.ic_gym_member_24dp,
            iconDescriptionResId = R.string.label_icon_student_bottom_sheet_register_user_description
        ),
        BottomSheetRegisterUserItem(
            option = EnumOptionsBottomSheetRegisterUser.PERSONAL_TRAINER,
            labelResId = R.string.label_trainer_bottom_sheet_register_user,
            iconResId = drawable.ic_instructor_24dp,
            iconDescriptionResId = R.string.label_icon_trainer_bottom_sheet_register_user_description
        ),
        BottomSheetRegisterUserItem(
            option = EnumOptionsBottomSheetRegisterUser.NUTRITIONIST,
            labelResId = R.string.label_nutritionist_bottom_sheet_register_user,
            iconResId = drawable.ic_nutritionist_24dp,
            iconDescriptionResId = R.string.label_icon_nutritionist_bottom_sheet_register_user_description
        )
    )

    BottomSheet(
        modifier = Modifier.testTag(BOTTOM_SHEET_REGISTER_USER.name),
        items = items,
        onDismissRequest = onDismissRequest,
        onItemClickListener = {
            Firebase.analytics.logBottomSheetItemClick(it)
            onItemClickListener?.onNavigate(RegisterUserScreenArgs(context = it))
            onDismissRequest()
        }
    )
}