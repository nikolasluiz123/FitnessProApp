package br.com.fitnesspro.compose.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.LabelTextStyle

@Composable
fun <T: Any> PagedLazyVerticalList(
    pagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    emptyMessageResId: Int? = null,
    emptyStateTextColor: Color = MaterialTheme.colorScheme.onBackground,
    verticalArrangementSpace: Dp = 0.dp,
    contentPadding: Dp = 0.dp,
    itemLayout: @Composable (T) -> Unit
) {
    if (pagingItems.itemCount > 0) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(verticalArrangementSpace),
            contentPadding = PaddingValues(contentPadding)
        ) {
            when (pagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is LoadState.Error -> {
                    item {
                        Text(
                            text = stringResource(R.string.paged_list_error_load_items),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    items(
                        count = pagingItems.itemCount,
                        key = pagingItems.itemKey(),
                        contentType = pagingItems.itemContentType()
                    ) { index ->
                        pagingItems[index]?.let { item ->
                            itemLayout(item)
                        }
                    }
                }
            }

            when (pagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is LoadState.Error -> {
                    item {
                        Text(
                            text = stringResource(R.string.paged_list_error_load_new_items),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {}
            }
        }
    } else {
        emptyMessageResId?.let { emptyMessage ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = emptyMessage),
                    style = LabelTextStyle,
                    textAlign = TextAlign.Center,
                    color = emptyStateTextColor
                )
            }
        }
    }
}