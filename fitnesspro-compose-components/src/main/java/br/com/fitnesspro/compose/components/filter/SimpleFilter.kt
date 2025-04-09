package br.com.fitnesspro.compose.components.filter

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.filter.EnumSimpleFilterTestTags.SIMPLE_FILTER_SEARCH_BAR
import br.com.fitnesspro.compose.components.filter.EnumSimpleFilterTestTags.SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD
import br.com.fitnesspro.compose.components.filter.EnumSimpleFilterTestTags.SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD_PLACEHOLDER
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.ValueTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleFilter(
    modifier: Modifier = Modifier,
    onSimpleFilterChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    expanded: Boolean,
    placeholderResId: Int,
    content: @Composable () -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }

    SearchBar(
        modifier = modifier.testTag(SIMPLE_FILTER_SEARCH_BAR.name),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            dividerColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
                query = text,
                onQueryChange = {
                    text = it
                    onSimpleFilterChange(text)
                },
                onSearch = {
                    onSimpleFilterChange(text)
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

@Preview(device = "id:small_phone")
@Composable
private fun SimpleFilterPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SimpleFilter(
                onSimpleFilterChange = {},
                placeholderResId = R.string.label_placeholder_example,
                expanded = false,
                onExpandedChange = {}
            ) {

            }
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun SimpleFilterPreviewLight() {
    FitnessProTheme {
        Surface {
            SimpleFilter(
                onSimpleFilterChange = {},
                placeholderResId = R.string.label_placeholder_example,
                expanded = false,
                onExpandedChange = {}
            ) {

            }
        }
    }
}