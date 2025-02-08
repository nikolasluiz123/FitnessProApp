package br.com.fitnesspro.firebase.api.firestore.documents

import com.google.common.reflect.TypeToken
import com.google.gson.Gson

abstract class FirestoreDocument {

    fun toMap(): Map<String, Any?> {
        val gson = Gson()
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<Map<String, Any?>>() {}.type)
    }
}