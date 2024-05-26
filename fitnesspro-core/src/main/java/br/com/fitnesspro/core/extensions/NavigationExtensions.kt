package br.com.fitnesspro.core.extensions

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator


/**
 * Função para definir um valor no state handle da backstack de navegação
 * e recuperar futuramente em algum ponto desejado.
 *
 * @param T Tipo do valor passado para o callback
 * @param key Chave que aponta para o valor setado em [androidx.lifecycle.SavedStateHandle]
 * @param callback Função que pode executar algo usando o valor retornado
 *
 * @author Nikolas Luiz Schmitt
 */
fun <T> NavController.setNavResultCallback(key: String, callback: (T) -> Unit) {
    currentBackStackEntry?.savedStateHandle?.set(key, callback)
}

/**
 * Função que retorna o callback que foi definido para que possa
 * ser executado.
 *
 * @param T Tipo do valor passado para o callback
 * @param key Chave que aponta para o valor setado em [androidx.lifecycle.SavedStateHandle]
 *
 * @author Nikolas Luiz Schmitt
 */
fun <T> NavController.getNavResultCallback(key: String): ((T) -> Unit)? {
    return previousBackStackEntry?.savedStateHandle?.remove(key)
}

/**
 * Função que recupera o callback utilizando [getNavResultCallback]
 * e executa passando como resultado algum valor.
 *
 * Após invocar o callback é feito o [NavController.popBackStack] para
 * remover da pilha a tela.
 *
 * @param T Tipo do valor passado para o callback
 * @param key Chave que aponta para o valor setado em [androidx.lifecycle.SavedStateHandle]
 * @param result O que desejar enviar para a tela anterior
 *
 * @author Nikolas Luiz Schmitt
 */
fun <T> NavController.popBackStackWithResult(key: String, result: T) {
    getNavResultCallback<T>(key)?.invoke(result)
    popBackStack()
}

/**
 * Função para navegar para um tela definindo um callback
 * usando [setNavResultCallback] para ser executado em algum
 * momento usando [popBackStackWithResult]
 *
 * @param T Tipo do valor passado para o callback
 * @param key Chave que aponta para o valor setado em [androidx.lifecycle.SavedStateHandle]
 * @param route Rota para onde deve ocorrer a navegação
 * @param callback Função que pode executar algo usando o valor retornado
 * @param navOptions Atributo que pode ser usado para definições da navegação
 * @param navigatorExtras Possibilita algum comportamento específico
 *
 * @author Nikolas Luiz Schmitt
 */
fun <T> NavController.navigateForResult(
    key: String,
    route: String,
    callback: (T) -> Unit,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    setNavResultCallback(key, callback)
    navigate(route, navOptions, navigatorExtras)
}