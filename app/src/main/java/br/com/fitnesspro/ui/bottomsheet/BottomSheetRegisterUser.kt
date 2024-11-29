package br.com.fitnesspro.ui.bottomsheet

import androidx.compose.runtime.Composable
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.bottomsheet.BottomSheet
import br.com.fitnesspro.core.R.*
import br.com.fitnesspro.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.ui.screen.login.callback.OnBottomSheetRegisterUserItemClick

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
    onItemClickListener: OnBottomSheetRegisterUserItemClick?
) {
    val items = listOf(
        BottomSheetRegisterUserItem(
            option = EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER,
            labelResId = R.string.label_student_bottom_sheet_register_user,
            iconResId = drawable.ic_student_24dp,
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
        items = items,
        onDismissRequest = onDismissRequest,
        onItemClickListener = {
            onItemClickListener?.onNavigate(RegisterUserScreenArgs(context = it))
            onDismissRequest()
        }
    )
}