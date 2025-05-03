package br.com.fitnesspro.firebase.api.firestore.documents

import br.com.fitnesspro.core.extensions.defaultGSon
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder

abstract class FirestoreDocument {

    fun toMap(): Map<String, Any?> {
        val gson = GsonBuilder().defaultGSon()
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<Map<String, Any?>>() {}.type)
    }
}