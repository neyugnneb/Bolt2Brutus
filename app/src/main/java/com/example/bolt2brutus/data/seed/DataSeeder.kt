package com.example.bolt2brutus.data.seed

import com.example.bolt2brutus.data.repository.RaceRepository
import com.example.bolt2brutus.data.repository.StatueRepository
import com.example.bolt2brutus.model.Race
import com.example.bolt2brutus.model.Statue

/** Fills the statues and races collections the first time the app runs against an empty database. */
class DataSeeder(
    private val statueRepository: StatueRepository = StatueRepository(),
    private val raceRepository: RaceRepository = RaceRepository(),
) {
    suspend fun seedIfEmpty() {
        if (!raceRepository.isEmpty()) return
        STATUES.forEach { statueRepository.save(it) }
        for (start in STATUES) {
            for (end in STATUES) {
                if (start.id == end.id) continue
                raceRepository.save(
                    Race(
                        id = "${start.id}_to_${end.id}",
                        raceName = "${start.name} to ${end.name}",
                        description = "Race from the ${start.name} Brutus to the ${end.name} Brutus.",
                        startStatue = start.toRef(),
                        endStatue = end.toRef(),
                    )
                )
            }
        }
    }

    private companion object {
        // TODO: Coordinates are approximate. Verify them on site before using them for GPS
        //  arrival detection, and add the remaining campus Brutus statues here.
        val STATUES = listOf(
            Statue(
                id = "ohio-union",
                name = "Ohio Union",
                latitude = 39.9977,
                longitude = -83.0086,
                locationDescription = "Inside the Ohio Union, 1739 N High St.",
                history = "TODO: add this statue's history.",
            ),
            Statue(
                id = "page-hall",
                name = "Page Hall",
                latitude = 40.0004,
                longitude = -83.0137,
                locationDescription = "Page Hall, home of the John Glenn College of Public Affairs.",
                history = "TODO: add this statue's history.",
            ),
        )
    }
}
