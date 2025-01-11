package br.com.fitnesspro.compose.components.filter

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_300
import br.com.fitnesspro.core.theme.GREY_600
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
        modifier = modifier,
        colors = SearchBarDefaults.colors(
            containerColor = GREY_300
        ),
        shape = SearchBarDefaults.fullScreenShape,
        inputField = {
            SearchBarDefaults.InputField(
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = br.com.fitnesspro.core.R.drawable.ic_search_24dp),
                        contentDescription = null
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(placeholderResId),
                        color = GREY_600,
                        style = ValueTextStyle
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

@Preview
@Composable
private fun SimpleFilterPreview() {
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