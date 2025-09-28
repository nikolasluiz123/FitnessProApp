package br.com.fitnesspro.compose.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fitnesspro.compose.components.gallery.video.components.EmptyState
import kotlinx.coroutines.flow.Flow

/**
 * Componente de listagem vertical.
 *
 * @param T Tipo do dado exibido.
 * @param modifier Modificadores específicos.
 * @param items Lista de itens que serão carregados.
 * @param verticalArrangementSpace Espaço entre cada item.
 * @param contentPadding Espaço ao redor da lista.
 * @param itemList Composable que define qual será o card do item. Pode ser usado outro tipo de container além do card.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun <T> LazyVerticalList(
    items: List<T>,
    emptyMessageResId: Int?,
    modifier: Modifier = Modifier,
    verticalArrangementSpace: Dp = 0.dp,
    contentPadding: Dp = 0.dp,
    reverseLayout: Boolean = false,
    itemList: @Composable (T) -> Unit
) {
    if (items.isNotEmpty()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(verticalArrangementSpace),
            contentPadding = PaddingValues(contentPadding),
            reverseLayout = reverseLayout
        ) {
            items(items = items) { item ->
                itemList(item)
            }
        }
    } else {
        emptyMessageResId?.let { emptyMessage ->
            EmptyState(emptyMessage = stringResource(id = emptyMessage))
        }
    }
}

@Composable
fun <T> LazyVerticalList(
    itemsFlow: Flow<List<T>>,
    emptyMessageResId: Int?,
    modifier: Modifier = Modifier,
    verticalArrangementSpace: Dp = 0.dp,
    contentPadding: Dp = 0.dp,
    reverseLayout: Boolean = false,
    itemList: @Composable (T) -> Unit
) {
    val items by itemsFlow.collectAsStateWithLifecycle(emptyList())

    if (items.isNotEmpty()) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(verticalArrangementSpace),
            contentPadding = PaddingValues(contentPadding),
            reverseLayout = reverseLayout
        ) {
            items(items = items) { item ->
                itemList(item)
            }
        }
    } else {
        emptyMessageResId?.let { emptyMessage ->
            EmptyState(emptyMessage = stringResource(id = emptyMessage))
        }
    }
}