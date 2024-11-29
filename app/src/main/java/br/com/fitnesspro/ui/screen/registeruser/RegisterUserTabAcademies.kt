package br.com.fitnesspro.ui.screen.registeruser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.ui.screen.registeruser.callback.OnAcademyItemClick
import br.com.fitnesspro.ui.state.RegisterUserUIState

@Composable
fun RegisterUserTabAcademies(
    state: RegisterUserUIState = RegisterUserUIState(),
    onAcademyItemClick: OnAcademyItemClick? = null
) {
    ConstraintLayout(
        Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        val (listRef) = createRefs()

//        LazyExpandableVerticalList(
//            modifier = Modifier.constrainAs(listRef) {
//                top.linkTo(parent.top)
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//                bottom.linkTo(parent.bottom)
//
//                width = Dimension.fillToConstraints
//                height = Dimension.fillToConstraints
//            },
//            groups = state.frequencies,
//            itemLayout =  { frequency ->
//                AcademyFrequencyItem(
//                    item = frequency,
//                    onClick = { onAcademyItemClick?.onExecute(RegisterAcademyScreenArgs(frequency)) }
//                )
//            }
//        )

    }
}

//@Composable
//fun AcademyFrequencyItem(item: Frequency, onClick: () -> Unit) {
//    ConstraintLayout(Modifier.padding(8.dp).clickable { onClick() }) {
//        val (dayWeekRef, timeRef, dividerRef) = createRefs()
//
//        createHorizontalChain(dayWeekRef, timeRef)
//
//        LabeledText(
//            modifier = Modifier.constrainAs(dayWeekRef) {
//                start.linkTo(parent.start)
//                top.linkTo(parent.top)
//
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 0.5f
//            },
//            label = stringResource(R.string.register_user_tab_academies_label_day_week),
//            value = item.dayWeekDisplay!!
//        )
//
//        LabeledText(
//            modifier = Modifier.constrainAs(timeRef) {
//                end.linkTo(parent.end)
//                top.linkTo(parent.top)
//
//                width = Dimension.fillToConstraints
//                horizontalChainWeight = 0.5f
//            },
//            label = stringResource(R.string.register_user_tab_academies_label_time),
//            value = stringResource(
//                R.string.register_user_tab_academies_value_time,
//                item.start!!.format(EnumDateTimePatterns.TIME),
//                item.end!!.format(EnumDateTimePatterns.TIME)
//            )
//        )
//
//        HorizontalDivider(
//            modifier = Modifier.constrainAs(dividerRef) {
//                bottom.linkTo(parent.bottom)
//                top.linkTo(dayWeekRef.bottom, margin = 8.dp)
//                start.linkTo(parent.start)
//                end.linkTo(parent.end)
//            }
//        )
//    }
//}

//@Preview
//@Composable
//private fun RegisterUserTabAcademiesPreview() {
//    val state = RegisterUserUIState(
//        frequencies = listOf(
//            AcademyFrequencyGroupDecorator(
//                label = R.string.register_academy_label_academy,
//                value = "Academia 1",
//                isExpanded = true,
//                items = listOf(
//                    Frequency(dayWeek = "Segunda", start = LocalTime.now(), end = LocalTime.now().plusHours(1)),
//                    Frequency(dayWeek = "Terça", start = LocalTime.now(), end = LocalTime.now().plusHours(1)),
//                )
//            ),
//            AcademyFrequencyGroupDecorator(
//                label = R.string.register_academy_label_academy,
//                value = "Academia 2",
//                isExpanded = false,
//                items = emptyList()
//            )
//        )
//    )
//
//    FitnessProTheme {
//        Surface {
//            RegisterUserTabAcademies(state)
//        }
//    }
//}