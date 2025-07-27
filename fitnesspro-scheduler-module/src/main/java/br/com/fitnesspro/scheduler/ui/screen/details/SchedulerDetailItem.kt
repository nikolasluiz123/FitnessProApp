package br.com.fitnesspro.scheduler.ui.screen.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.divider.FitnessProHorizontalDivider
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.firebase.api.analytics.logListItemClick
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.navigation.CompromiseScreenArgs
import br.com.fitnesspro.scheduler.ui.screen.details.callbacks.OnNavigateToCompromise
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST_COMPROMISE_TYPE
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST_LABELED_TEXT_HOUR
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST_LABELED_TEXT_NAME
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST_LABELED_TEXT_PROFESSIONAL
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST_LABELED_TEXT_SITUATION
import br.com.fitnesspro.scheduler.ui.state.SchedulerDetailsUIState
import br.com.fitnesspro.to.TOScheduler
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

@Composable
internal fun SchedulerDetailItem(
    to: TOScheduler,
    state: SchedulerDetailsUIState,
    onNavigateToCompromise: OnNavigateToCompromise? = null
) {
    val context = LocalContext.current

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag(SCHEDULER_DETAILS_SCREEN_ITEM_LIST.name)
            .clickable {
                Firebase.analytics.logListItemClick(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)

                onNavigateToCompromise?.onExecute(
                    args = CompromiseScreenArgs(
                        date = state.subtitle.parseToLocalDate(EnumDateTimePatterns.DATE)!!,
                        recurrent = false,
                        schedulerId = to.id
                    )
                )
            }
    ) {
        val (nameRef, hourRef, compromiseTypeRef, situationRef, professionalRef,
             dividerRef) = createRefs()

        createHorizontalChain(nameRef, hourRef)

        LabeledText(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .testTag(SCHEDULER_DETAILS_SCREEN_ITEM_LIST_LABELED_TEXT_NAME.name)
                .constrainAs(nameRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.scheduler_details_name_label),
            value = if (state.userType == EnumUserType.ACADEMY_MEMBER) to.professionalName!! else to.academyMemberName!!
        )

        LabeledText(
            modifier = Modifier
                .testTag(SCHEDULER_DETAILS_SCREEN_ITEM_LIST_LABELED_TEXT_HOUR.name)
                .padding(end = 8.dp)
                .constrainAs(hourRef) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.scheduler_details_hour_label),
            value = stringResource(
                R.string.scheduler_details_hour_value,
                to.dateTimeStart!!.format(EnumDateTimePatterns.TIME),
                to.dateTimeEnd!!.format(EnumDateTimePatterns.TIME)
            )
        )

        createHorizontalChain(compromiseTypeRef, situationRef)

        LabeledText(
            modifier = Modifier
                .testTag(SCHEDULER_DETAILS_SCREEN_ITEM_LIST_COMPROMISE_TYPE.name)
                .padding(start = 8.dp, end = 8.dp)
                .constrainAs(compromiseTypeRef) {
                    top.linkTo(nameRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.scheduler_details_compromisse_type_label),
            value = to.compromiseType?.getLabel(context)!!
        )

        LabeledText(
            modifier = Modifier
                .testTag(SCHEDULER_DETAILS_SCREEN_ITEM_LIST_LABELED_TEXT_SITUATION.name)
                .padding(end = 8.dp)
                .constrainAs(situationRef) {
                    top.linkTo(hourRef.bottom, margin = 12.dp)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.scheduler_details_situation_label),
            value = to.situation?.getLabel(context)!!
        )

        if (state.userType == EnumUserType.ACADEMY_MEMBER) {
            LabeledText(
                modifier = Modifier
                    .testTag(SCHEDULER_DETAILS_SCREEN_ITEM_LIST_LABELED_TEXT_PROFESSIONAL.name)
                    .padding(horizontal = 8.dp)
                    .constrainAs(professionalRef) {
                        top.linkTo(compromiseTypeRef.bottom, margin = 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                },
                label = stringResource(R.string.scheduler_details_professional_label),
                value = to.professionalName!!
            )
        }

        FitnessProHorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                if (state.userType == EnumUserType.ACADEMY_MEMBER) {
                    top.linkTo(professionalRef.bottom, margin = 8.dp)
                } else {
                    top.linkTo(compromiseTypeRef.bottom, margin = 8.dp)
                }

                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        )
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerDetailItemAcademyMemberPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerDetailItem(
                to = toSchedulerAcademyMember,
                state = academyMemberState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerDetailItemProfessionalPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerDetailItem(
                to = toSchedulerProfessional,
                state = professionalState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerDetailItemAcademyMemberPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerDetailItem(
                to = toSchedulerAcademyMember,
                state = academyMemberState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerDetailItemProfessionalPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerDetailItem(
                to = toSchedulerProfessional,
                state = professionalState
            )
        }
    }
}