package br.com.fitnesspro.compose.components.bottomsheet.interfaces

/**
 * Interface que define atributos e comportamentos padrões
 * de qualquer item de bottomsheet.
 *
 * @property option Enum que representa o item.
 * @property iconResId Id do ícone do item.
 * @property labelResId Id do label do item.
 * @property iconDescriptionResId Id da descrição do ícone.
 *
 * @author Nikolas Luiz Schmitt
 */
interface IBottomSheetItem<T: IEnumOptionsBottomSheet> {
    val option: T
    val iconResId: Int
    val labelResId: Int
    val iconDescriptionResId: Int
}