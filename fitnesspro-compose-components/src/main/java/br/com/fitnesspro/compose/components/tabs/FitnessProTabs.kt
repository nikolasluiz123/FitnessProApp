package br.com.fitnesspro.compose.components.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.theme.GREY_500
import br.com.fitnesspro.core.theme.TabTitleTextStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Componente Wrapper para o [TabRow] onde criamos as tabs.
 *
 * @param modifier Modifier para posicionamento e demais configurações que forem necessárias
 * @param tabs Lista de tabs que farão parte do [TabRow]
 * @param coroutineScope CoroutineScope para controle do [TabRow]
 * @param pagerState PagerState para controle do [TabRow]
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FitnessProTabRow(
    modifier: Modifier = Modifier,
    tabs: MutableList<Tab>,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = tabs.first { it.selected.value }.enum.index,
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        divider = {
            HorizontalDivider(color = MaterialTheme.colorScheme.onSecondary)
        }
    ) {
        tabs.forEach { tabToCreate ->
            Tab(
                selected = tabToCreate.selected.value,
                onClick = {
                    tabs.forEach { it.selected.value = false }
                    tabToCreate.selected.value = true

                    coroutineScope.launch {
                        pagerState.animateScrollToPage(tabToCreate.enum.index)
                    }
                },
                text = {
                    Text(
                        text = stringResource(id = tabToCreate.enum.labelResId),
                        style = TabTitleTextStyle,
                        color = if (tabToCreate.isEnabled()) Color.White else GREY_500
                    )
                },
                enabled = tabToCreate.isEnabled()
            )
        }
    }
}

/**
 * Componente Wrapper para o [HorizontalPager] que possibilita facilitar o uso do scroll para mudar as
 * tabs do [TabRow], além do conteúdo de cada tab.
 *
 * @param pagerState PagerState para controle do [HorizontalPager]
 * @param tabs Lista de tabs que farão parte do [HorizontalPager]
 * @param modifier Modifier para posicionamento e demais configurações que forem necessárias
 * @param content Composable que será renderizado para cada tab
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FitnessProHorizontalPager(
    pagerState: PagerState,
    tabs: List<Tab>,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSpacing = 0.dp,
        userScrollEnabled = getUserScrollEnabled(tabs),
        reverseLayout = false,
        contentPadding = PaddingValues(0.dp),
        beyondBoundsPageCount = 0,
        pageSize = PageSize.Fill,
        flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
        key = null,
        pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
            state = pagerState,
            orientation = Orientation.Horizontal
        )
    ) { index ->
        tabs.forEach { it.selected.value = false }
        tabs.getOrNull(index)?.selected?.value = true

        content(index)
    }
}

/**
 * Função utilizada para recuperar se o scroll do [HorizontalPager] deve ser habilitado ou não.
 *
 * Basicamente ele deve ser habilitado se a próxima tab ou a tab anterior estiver habilitada.
 */
private fun getUserScrollEnabled(tabs: List<Tab>): Boolean {
    val tabSelected = tabs.first { it.selected.value }
    val nextTab = tabs.getOrNull(tabs.indexOf(tabSelected) + 1)
    val previousTab = tabs.getOrNull(tabs.indexOf(tabSelected) - 1)

    return nextTab?.isEnabled?.invoke() == true || previousTab?.isEnabled?.invoke() == true
}