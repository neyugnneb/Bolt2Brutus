package com.example.bolt2brutus.ui.map

import androidx.annotation.StringRes
import com.example.bolt2brutus.R
import com.google.android.gms.maps.model.LatLng

internal data class StatueSite(
    val name: String,
    val address: String,
    val position: LatLng,
    val statues: List<StatueListing>,
    @StringRes val accessNote: Int? = null,
)

internal data class StatueListing(val name: String, val details: String? = null)

// Ohio Staters' April 21, 2025 "Where Is Brutus?" flyer supplies the names and room details.
// Approximate venue coordinates use the campus maps and OpenStreetMap (https://www.openstreetmap.org/copyright).
internal val statueSites = listOf(
    StatueSite(
        "Adventure Recreation Center", "855 Woody Hayes Drive",
        LatLng(40.00264, -83.03190),
        listOf(StatueListing("Adventure Brutus")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Ohio State University Airport", "2160 W. Case Road",
        LatLng(40.07334, -83.07300),
        listOf(StatueListing("Barnstormer Brutus", "Austin E. Knowlton Executive Terminal lobby")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Doan Hall", "410 W. 10th Avenue",
        LatLng(39.99572, -83.01763),
        listOf(StatueListing("Nutrition Services Brutus", "BistrOH! cafeteria")),
        R.string.map_access_medical,
    ),
    StatueSite(
        "Blankenship Hall", "901 Woody Hayes Drive",
        LatLng(40.00210, -83.03330),
        listOf(StatueListing("Public Safety Brutus", "Main lobby")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Brain and Spine Hospital", "300 W. 10th Avenue",
        LatLng(39.99559, -83.01645),
        listOf(StatueListing("Doctor Brutus")),
        R.string.map_access_medical,
    ),
    StatueSite(
        "Bricker Hall", "190 N. Oval Mall",
        LatLng(40.00064, -83.01324),
        listOf(StatueListing("Pizza Chef Brutus", "Room 001")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Buckeye Corner", "1315 W. Lane Avenue",
        LatLng(40.00728, -83.04640),
        listOf(StatueListing("Super Fan Brutus")),
    ),
    StatueSite(
        "College Traditions", "286 W. Lane Avenue",
        LatLng(40.00664, -83.01715),
        listOf(StatueListing("Dot the “I” Brutus")),
    ),
    StatueSite(
        "Columbus Historical Society", "717 W. Town Street",
        LatLng(39.95604, -83.01917),
        listOf(StatueListing("Christopher Columbus Brutus")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Converse Hall", "2121 Tuttle Park Place",
        LatLng(40.00511, -83.01749),
        listOf(StatueListing("Freedom Brutus", "Outside the building")),
    ),
    StatueSite(
        "Fawcett Center", "2400 Olentangy River Road",
        LatLng(40.01081, -83.02083),
        listOf(
            StatueListing("Blarney Brutus", "Displayed for St. Patrick’s Day"),
            StatueListing("Gold Brutus", "Main lobby"),
            StatueListing("Justice Brutus", "Ninth floor"),
        ),
        R.string.map_access_business,
    ),
    StatueSite(
        "University Square North", "14 E. 15th Avenue",
        LatLng(40.00059, -83.00771),
        listOf(StatueListing("Brainy Brutus", "Ohio State Foundation")),
        R.string.map_access_business,
    ),
    StatueSite(
        "University Square South", "15 E. 15th Avenue",
        LatLng(39.99972, -83.00753),
        listOf(StatueListing("Brutus Buckeye")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Gateway", "1590 N. High Street",
        LatLng(39.99459, -83.00667),
        listOf(
            StatueListing("Coach Tressel Brutus", "Office of Investments, suite 420"),
            StatueListing("Super Hero Brutus", "Office of the Treasurer, suite 400"),
        ),
        R.string.map_access_business,
    ),
    StatueSite(
        "Ohio State University Golf Club", "3605 Tremont Road",
        LatLng(40.03190, -83.05230),
        listOf(StatueListing("Fore Brutus", "Clubhouse")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Journalism Building", "242 W. 18th Avenue",
        LatLng(40.00200, -83.01504),
        listOf(StatueListing("Mild Mannered Reporter Brutus")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Keenan Center for Entrepreneurship", "1636 N. High Street",
        LatLng(39.99558, -83.00691),
        listOf(StatueListing("Love and Peace Brutus")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Longaberger Alumni House", "2200 Olentangy River Road",
        LatLng(40.00877, -83.02133),
        listOf(StatueListing("Alumni Brutus", "Main lobby")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Ohio State Outpatient Care East (CarePoint East)", "543 Taylor Avenue",
        LatLng(39.97856, -82.96312),
        listOf(StatueListing("Brutus Buckeye")),
        R.string.map_access_medical,
    ),
    StatueSite(
        "Rhodes Hall", "410 W. 10th Avenue",
        LatLng(39.99511, -83.01792),
        listOf(StatueListing("Brutus Buckeye", "Main lobby")),
        R.string.map_access_medical,
    ),
    StatueSite(
        "Meiling Hall", "370 W. 9th Avenue",
        LatLng(39.99475, -83.01693),
        listOf(StatueListing("Doctor Brutus", "Health Science Administration, suite 200")),
        R.string.map_access_medical,
    ),
    StatueSite(
        "Ohio Union", "1739 N. High Street",
        LatLng(39.99836, -83.00776),
        listOf(
            StatueListing("Butter Brutus", "Instructional Kitchen, room 0165"),
            StatueListing("Brutus Buckeye", "Undergraduate Admissions Welcome Center"),
            StatueListing("Woody Hayes Brutus", "Woody’s Tavern, room 1135"),
        ),
        R.string.map_access_business,
    ),
    StatueSite(
        "Page Hall", "1810 College Road",
        LatLng(39.99916, -83.00965),
        listOf(StatueListing("First Buckeye in Space Brutus", "Suite 110")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Schottenstein Center", "555 Borror Drive",
        LatLng(40.00751, -83.02510),
        listOf(
            StatueListing("Basketball Brutus", "Near gate 127"),
            StatueListing("Rock N’ Roll Brutus", "Northeast rotunda"),
        ),
        R.string.map_access_business,
    ),
    StatueSite(
        "The Blackwell", "2110 Tuttle Park Place",
        LatLng(40.00479, -83.01669),
        listOf(StatueListing("Blackwell Brutus")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Theatre, Film, and Media Arts Building", "1932 College Road N.",
        LatLng(40.00150, -83.00966),
        listOf(StatueListing("Break A Leg Brutus")),
        R.string.map_access_business,
    ),
    StatueSite(
        "Wendy’s", "1483 Olentangy River Road",
        LatLng(39.99081, -83.02501),
        listOf(StatueListing("Dave Thomas Brutus")),
        R.string.map_access_business,
    ),
)
