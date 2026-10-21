package com.example.bolt2brutus.model

import com.google.firebase.firestore.DocumentId

/** Firestore document: races/{raceId}. Start and end statues are embedded copies. */
data class Race(
    @DocumentId var id: String = "",
    var raceName: String = "",
    var description: String = "",
    var startStatue: StatueRef = StatueRef(),
    var endStatue: StatueRef = StatueRef(),
)
