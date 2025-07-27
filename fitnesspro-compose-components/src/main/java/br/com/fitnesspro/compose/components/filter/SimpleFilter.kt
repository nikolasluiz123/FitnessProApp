package br.com.fitnesspro.compose.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.filter.EnumSimpleFilterTestTags.SIMPLE_FILTER_SEARCH_BAR
import br.com.fitnesspro.compose.components.filter.EnumSimpleFilterTestTags.SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD
import br.com.fitnesspro.compose.components.filter.EnumSimpleFilterTestTags.SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD_PLACEHOLDER
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.ValueTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleFilter(
    state: SimpleFilterState,
    placeholderResId: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    SimpleFilter(
        expanded = state.simpleFilterExpanded,
        quickFilter = state.quickFilter,
        placeholderResId = placeholderResId,
        onSimpleFilterChange = state.onSimpleFilterChange,
        onExpandedChange = state.onExpandedChange,
        modifier = modifier,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleFilter(
    expanded: Boolean,
    quickFilter: String,
    placeholderResId: Int,
    onSimpleFilterChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    SearchBar(
        windowInsets = WindowInsets(top = 0.dp),
        modifier = modifier
            .testTag(SIMPLE_FILTER_SEARCH_BAR.name)
            .background(color = MaterialTheme.colorScheme.secondaryContainer),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            dividerColor = Color.Transparent,
        ),
        shape = SearchBarDefaults.fullScreenShape,
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.testTag(SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD.name),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = br.com.fitnesspro.core.R.drawable.ic_search_24dp),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                placeholder = {
                    Text(
                        modifier = Modifier.testTag(SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD_PLACEHOLDER.name),
                        text = stringResource(placeholderResId),
                        style = ValueTextStyle,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                query = quickFilter,
                onQueryChange = {
                    onSimpleFilterChange(it)
                },
                onSearch = {
                    onSimpleFilterChange(it)
                },
                expanded = expanded,
                onExpandedChange = onExpandedChange
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        content = {
            content()
        }
    )
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SimpleFilterPreviewWithTextDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SimpleFilter(
                quickFilter = "Teste",
                onSimpleFilterChange = {},
                placeholderResId = R.string.label_placeholder_example,
                expanded = false,
                onExpandedChange = {}
            ) {

            }
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SimpleFilterPreviewWithLight() {
    FitnessProTheme {
        Surface {
            SimpleFilter(
                quickFilter = "Teste",
                onSimpleFilterChange = {},
                placeholderResId = R.string.label_placeholder_example,
                expanded = false,
                onExpandedChange = {}
            ) {

            }
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SimpleFilterPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SimpleFilter(
                quickFilter = "",
                onSimpleFilterChange = {},
                placeholderResId = R.string.label_placeholder_example,
                expanded = false,
                onExpandedChange = {}
            ) {

            }
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SimpleFilterPreviewLight() {
    FitnessProTheme {
        Surface {
            SimpleFilter(
                quickFilter = "",
                onSimpleFilterChange = {},
                placeholderResId = R.string.label_placeholder_example,
                expanded = false,
                onExpandedChange = {}
            ) {

            }
        }
    }
}