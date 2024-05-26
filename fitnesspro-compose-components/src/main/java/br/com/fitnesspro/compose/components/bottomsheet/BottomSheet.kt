package br.com.fitnesspro.compose.components.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
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
fun <T: IEnumOptionsBottomSheet> BottomSheet(
    items: List<IBottomSheetItem<T>>,
    onDismissRequest: () -> Unit,
    onItemClickListener: (T) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn {
            items(items) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = it.labelResId),
                            style = BottomSheetItemTextStyle,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = it.iconResId),
                            contentDescription = stringResource(id = it.iconDescriptionResId),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.clickable {
                        onItemClickListener(it.option)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.size(48.dp))
    }
}