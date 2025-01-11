package br.com.fitnesspro.local.data.access.dao

import java.util.StringJoiner

abstract class BaseDAO {

    companion object {
        const val QR_NL = "\r\n"
    }

    fun StringJoiner.concatElementsForIn(elements: List<Any>, params: MutableList<Any>) {
        add("(")

        elements.forEachIndexed { index, element ->
            if (elements.size > 1 && elements.lastIndex != index) add("?,") else add("?")
            params.add(element)
        }

        add(")")
    }
}