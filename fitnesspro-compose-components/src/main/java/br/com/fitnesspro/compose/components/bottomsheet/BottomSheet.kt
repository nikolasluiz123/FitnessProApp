package br.com.fitnesspro.compose.components.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IBottomSheetItem
import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IEnumOptionsBottomSheet
import br.com.fitnesspro.core.theme.BottomSheetItemTextStyle

/**
 * Componente de bottomsheet que pode ser utilizado
 * de forma genérica
 *
 * @param items Itens que serão exibidos
 * @param onDismissRequest Callback executado ao sair do bottomsheet
 * @param onItemClickListener Callback executado ao clicar no item
 *
 * @author Nikolas Luiz Schmitt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : IEnumOptionsBottomSheet> BottomSheet(
    items: List<IBottomSheetItem<T>>,
    onDismissRequest: () -> Unit,
    onItemClickListener: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        LazyColumn {
            items(items) { item ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = item.labelResId),
                            style = BottomSheetItemTextStyle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = item.iconResId?.let { iconResId ->
                        {
                            Icon(
                                painter = painterResource(id = iconResId),
                                contentDescription = item.iconDescriptionResId?.let { stringResource(id = it) },
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier.clickable {
                        onItemClickListener(item.option)
                    }
                )
            }
        }
    }
}