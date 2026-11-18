package com.example.bolt2brutus.ui.map

import androidx.annotation.StringRes
import com.example.bolt2brutus.R
import com.example.bolt2brutus.model.Statue
import com.example.bolt2brutus.model.StatueAccess
import com.google.android.gms.maps.model.LatLng

/** One map pin: a building and the statues inside it. */
internal data class StatueSite(
    val name: String,
    val address: String,
    val position: LatLng,
    val statues: List<Statue>,
    @StringRes val accessNote: Int? = null,
)

internal fun List<Statue>.toSites(): List<StatueSite> =
    groupBy { it.siteName }.map { (siteName, statues) ->
        val first = statues.first()
        StatueSite(
            name = siteName,
            address = first.address,
            position = LatLng(first.latitude, first.longitude),
            statues = statues,
            accessNote = when (first.accessNote) {
                StatueAccess.BUSINESS -> R.string.map_access_business
                StatueAccess.MEDICAL -> R.string.map_access_medical
                else -> null
            },
        )
    }
