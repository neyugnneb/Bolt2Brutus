package com.example.bolt2brutus.model

import com.google.firebase.firestore.DocumentId

/**
 * Firestore document: statues/{statueId}.
 *
 * Several statues can share a building; [siteName], [address], and [accessNote]
 * describe that building so the map can show one pin per site.
 */
data class Statue(
    @DocumentId var id: String = "",
    var name: String = "",
    var siteName: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var locationDescription: String = "",
    var accessNote: String = StatueAccess.OPEN,
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

/** Values for [Statue.accessNote]. */
object StatueAccess {
    const val OPEN = ""
    const val BUSINESS = "business"
    const val MEDICAL = "medical"
}
