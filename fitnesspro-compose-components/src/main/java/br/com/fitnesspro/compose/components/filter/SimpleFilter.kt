package br.com.fitnesspro.compose.components.filter

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleFilter(
    modifier: Modifier = Modifier,
    onSimpleFilterChange: (String) -> Unit,
    expanded: Boolean,
    content: @Composable () -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }

    SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = text,
                onQueryChange = {
                    text = it
                    onSimpleFilterChange(text)
                },
                onSearch = {
                    onSimpleFilterChange(text)
                },
                expanded = expanded,
                onExpandedChange = {

                }
            )
        },
        expanded = expanded,
        onExpandedChange = {

        },
        content = {
            content()
        }
    )
}