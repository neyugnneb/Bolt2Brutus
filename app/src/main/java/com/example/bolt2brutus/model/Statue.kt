package com.example.bolt2brutus.model

import com.google.firebase.firestore.DocumentId

/** Firestore document: statues/{statueId}. */
data class Statue(
    @DocumentId var id: String = "",
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var locationDescription: String = "",
    var history: String = "",
    var imageUrl: String = "",
) {
    fun toRef() = StatueRef(id, name, latitude, longitude)
}

/** Copy of the statue fields a race needs, embedded so a race loads without extra reads. */
data class StatueRef(
    var statueId: String = "",
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
)
