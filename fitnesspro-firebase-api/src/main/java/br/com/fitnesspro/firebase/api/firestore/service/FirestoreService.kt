package br.com.fitnesspro.firebase.api.firestore.service

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

abstract class FirestoreService {

    protected val db = Firebase.firestore

    /**
     * Função que retorna o horário do servidor do banco do firestore. Pode ser utilizado em todos os
     * campos de data para padronizar o valor.
     */
    protected suspend fun getServerTime(): Long {
        val dummyDocRef = db.collection("serverTime").document("timestamp")

        val data = mapOf("timestamp" to FieldValue.serverTimestamp())
        dummyDocRef.set(data).await()

        val snapshot = dummyDocRef.get().await()
        val serverTimestamp = snapshot.getTimestamp("timestamp")?.seconds!! * 1000

        return serverTimestamp
    }
}