package br.com.fitnesspor.service.data.access.adapters

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class GenericInterfaceAdapterFactory<I : Any, T : I>(
    private val interfaceType: Class<I>,
    private val implementationType: Class<T>
) : TypeAdapterFactory {

    override fun <R> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        if (type.rawType == interfaceType) {
            val delegate = gson.getDelegateAdapter(this, TypeToken.get(implementationType))

            @Suppress("UNCHECKED_CAST")
            return object : TypeAdapter<I>() {
                override fun write(out: JsonWriter, value: I?) {
                    delegate.write(out, value as? T)
                }

                override fun read(reader: JsonReader): I {
                    return delegate.read(reader)
                }
            } as TypeAdapter<R>
        }

        return null
    }
}