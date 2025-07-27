package br.com.fitnesspro.compose.components.tabs

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.divider.FitnessProHorizontalDivider
import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.tabs.EnumTabTestTags.HORIZONTAL_PAGER
import br.com.fitnesspro.compose.components.tabs.EnumTabTestTags.TAB
import br.com.fitnesspro.compose.components.tabs.EnumTabTestTags.TAB_ROW
import br.com.fitnesspro.core.theme.TabTitleTextStyle
import br.com.fitnesspro.firebase.api.analytics.logTabClick
import br.com.fitnesspro.firebase.api.analytics.logTabScroll
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Componente Wrapper para o [TabRow] onde criamos as tabs.
 *
 * @param modifier Modifier para posicionamento e demais configurações que forem necessárias
 * @param tabState Objeto de state que contem os comportamentos necessários para o [TabRow]
 * @param coroutineScope CoroutineScope para controle do [TabRow]
 * @param pagerState PagerState para controle do [TabRow]
 */
@Composable
fun FitnessProTabRow(
    modifier: Modifier = Modifier,
    tabState: TabState,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
) {
    TabRow(
        modifier = modifier.testTag(TAB_ROW.name),
        selectedTabIndex = tabState.tabs.first { it.selected }.enum.index,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        divider = {
            FitnessProHorizontalDivider()
        },
        indicator = { tabPositions ->
            val selectedIndex = tabState.tabs.first { it.selected }.enum.index

            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
    ) {
        tabState.tabs.forEach { tabToCreate ->
            Tab(
                modifier = Modifier.testTag(TAB.name),
                selected = tabToCreate.selected,
                onClick = {
                    Firebase.analytics.logTabClick(tabToCreate.enum as Enum<*>)
                    tabState.onSelectTab(tabToCreate)

                    coroutineScope.launch {
                        pagerState.scrollToPage(tabToCreate.enum.index)
                    }
                },
                text = {
                    Text(
                        text = stringResource(id = tabToCreate.enum.labelResId),
                        style = TabTitleTextStyle,
                        color = if (tabToCreate.enabled) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        }
                    )
                },
                enabled = tabToCreate.enabled
            )
        }
    }
}

/**
 * Componente Wrapper para o [HorizontalPager] que possibilita facilitar o uso do scroll para mudar as
 * tabs do [TabRow], além do conteúdo de cada tab.
 *
 * @param pagerState PagerState para controle do [HorizontalPager]
 * @param tabState Objeto de state que contem os comportamentos necessários para o [HorizontalPager]
 * @param modifier Modifier para posicionamento e demais configurações que forem necessárias
 * @param content Composable que será renderizado para cada tab
 */
@Composable
fun FitnessProHorizontalPager(
    pagerState: PagerState,
    tabState: TabState,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit
) {
    HorizontalPager(
        modifier = modifier.testTag(HORIZONTAL_PAGER.name),
        state = pagerState,
        pageSpacing = 0.dp,
        userScrollEnabled = getUserScrollEnabled(tabState.tabs),
        reverseLayout = false,
        contentPadding = PaddingValues(0.dp),
        pageSize = PageSize.Fill,
        flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
        key = null,
        pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
            state = pagerState,
            orientation = Orientation.Horizontal
        )
    ) { index ->
        Firebase.analytics.logTabScroll(tabState.tabs[index].enum as Enum<*>)
        tabState.onSelectTab(tabState.tabs[index])
        content(index)
    }
}

/**
 * Função utilizada para recuperar se o scroll do [HorizontalPager] deve ser habilitado ou não.
 *
 * Basicamente ele deve ser habilitado se a próxima tab ou a tab anterior estiver habilitada.
 */
private fun getUserScrollEnabled(tabs: List<Tab>): Boolean {
    val tabSelected = tabs.first { it.selected }
    val nextTab = tabs.getOrNull(tabs.indexOf(tabSelected) + 1)
    val previousTab = tabs.getOrNull(tabs.indexOf(tabSelected) - 1)

    return nextTab?.enabled == true || previousTab?.enabled == true
}