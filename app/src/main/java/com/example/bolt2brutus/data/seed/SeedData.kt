package com.example.bolt2brutus.data.seed

import com.example.bolt2brutus.model.Statue
import com.example.bolt2brutus.model.StatueAccess

// Ohio Staters' April 21, 2025 "Where Is Brutus?" flyer supplies the names and room details.
// Approximate venue coordinates use the campus maps and OpenStreetMap (https://www.openstreetmap.org/copyright).
// TODO: Verify coordinates on site before relying on them for GPS arrival detection, and add each statue's history.
internal val SEED_STATUES: List<Statue> = listOf(
    site(
        "Adventure Recreation Center", "855 Woody Hayes Drive", 40.00264, -83.03190, StatueAccess.BUSINESS,
        "Adventure Brutus" to "",
    ),
    site(
        "Ohio State University Airport", "2160 W. Case Road", 40.07334, -83.07300, StatueAccess.BUSINESS,
        "Barnstormer Brutus" to "Austin E. Knowlton Executive Terminal lobby",
    ),
    site(
        "Doan Hall", "410 W. 10th Avenue", 39.99572, -83.01763, StatueAccess.MEDICAL,
        "Nutrition Services Brutus" to "BistrOH! cafeteria",
    ),
    site(
        "Blankenship Hall", "901 Woody Hayes Drive", 40.00210, -83.03330, StatueAccess.BUSINESS,
        "Public Safety Brutus" to "Main lobby",
    ),
    site(
        "Brain and Spine Hospital", "300 W. 10th Avenue", 39.99559, -83.01645, StatueAccess.MEDICAL,
        "Doctor Brutus" to "",
    ),
    site(
        "Bricker Hall", "190 N. Oval Mall", 40.00064, -83.01324, StatueAccess.BUSINESS,
        "Pizza Chef Brutus" to "Room 001",
    ),
    site(
        "Buckeye Corner", "1315 W. Lane Avenue", 40.00728, -83.04640, StatueAccess.OPEN,
        "Super Fan Brutus" to "",
    ),
    site(
        "College Traditions", "286 W. Lane Avenue", 40.00664, -83.01715, StatueAccess.OPEN,
        "Dot the “I” Brutus" to "",
    ),
    site(
        "Columbus Historical Society", "717 W. Town Street", 39.95604, -83.01917, StatueAccess.BUSINESS,
        "Christopher Columbus Brutus" to "",
    ),
    site(
        "Converse Hall", "2121 Tuttle Park Place", 40.00511, -83.01749, StatueAccess.OPEN,
        "Freedom Brutus" to "Outside the building",
    ),
    site(
        "Fawcett Center", "2400 Olentangy River Road", 40.01081, -83.02083, StatueAccess.BUSINESS,
        "Blarney Brutus" to "Displayed for St. Patrick’s Day",
        "Gold Brutus" to "Main lobby",
        "Justice Brutus" to "Ninth floor",
    ),
    site(
        "University Square North", "14 E. 15th Avenue", 40.00059, -83.00771, StatueAccess.BUSINESS,
        "Brainy Brutus" to "Ohio State Foundation",
    ),
    site(
        "University Square South", "15 E. 15th Avenue", 39.99972, -83.00753, StatueAccess.BUSINESS,
        "Brutus Buckeye" to "",
    ),
    site(
        "Gateway", "1590 N. High Street", 39.99459, -83.00667, StatueAccess.BUSINESS,
        "Coach Tressel Brutus" to "Office of Investments, suite 420",
        "Super Hero Brutus" to "Office of the Treasurer, suite 400",
    ),
    site(
        "Ohio State University Golf Club", "3605 Tremont Road", 40.03190, -83.05230, StatueAccess.BUSINESS,
        "Fore Brutus" to "Clubhouse",
    ),
    site(
        "Journalism Building", "242 W. 18th Avenue", 40.00200, -83.01504, StatueAccess.BUSINESS,
        "Mild Mannered Reporter Brutus" to "",
    ),
    site(
        "Keenan Center for Entrepreneurship", "1636 N. High Street", 39.99558, -83.00691, StatueAccess.BUSINESS,
        "Love and Peace Brutus" to "",
    ),
    site(
        "Longaberger Alumni House", "2200 Olentangy River Road", 40.00877, -83.02133, StatueAccess.BUSINESS,
        "Alumni Brutus" to "Main lobby",
    ),
    site(
        "Ohio State Outpatient Care East (CarePoint East)", "543 Taylor Avenue", 39.97856, -82.96312,
        StatueAccess.MEDICAL,
        "Brutus Buckeye" to "",
    ),
    site(
        "Rhodes Hall", "410 W. 10th Avenue", 39.99511, -83.01792, StatueAccess.MEDICAL,
        "Brutus Buckeye" to "Main lobby",
    ),
    site(
        "Meiling Hall", "370 W. 9th Avenue", 39.99475, -83.01693, StatueAccess.MEDICAL,
        "Doctor Brutus" to "Health Science Administration, suite 200",
    ),
    site(
        "Ohio Union", "1739 N. High Street", 39.99836, -83.00776, StatueAccess.BUSINESS,
        "Butter Brutus" to "Instructional Kitchen, room 0165",
        "Brutus Buckeye" to "Undergraduate Admissions Welcome Center",
        "Woody Hayes Brutus" to "Woody’s Tavern, room 1135",
    ),
    site(
        "Page Hall", "1810 College Road", 39.99916, -83.00965, StatueAccess.BUSINESS,
        "First Buckeye in Space Brutus" to "Suite 110",
    ),
    site(
        "Schottenstein Center", "555 Borror Drive", 40.00751, -83.02510, StatueAccess.BUSINESS,
        "Basketball Brutus" to "Near gate 127",
        "Rock N’ Roll Brutus" to "Northeast rotunda",
    ),
    site(
        "The Blackwell", "2110 Tuttle Park Place", 40.00479, -83.01669, StatueAccess.BUSINESS,
        "Blackwell Brutus" to "",
    ),
    site(
        "Theatre, Film, and Media Arts Building", "1932 College Road N.", 40.00150, -83.00966,
        StatueAccess.BUSINESS,
        "Break A Leg Brutus" to "",
    ),
    site(
        "Wendy’s", "1483 Olentangy River Road", 39.99081, -83.02501, StatueAccess.BUSINESS,
        "Dave Thomas Brutus" to "",
    ),
).flatten()

/**
 * Curated walking races between main-campus statues, as (start statue ID, end statue ID).
 * Medical buildings and off-campus sites are left out on purpose.
 */
internal val SEED_RACES: List<Pair<String, String>> = listOf(
    "ohio-union-brutus-buckeye" to "page-hall-first-buckeye-in-space-brutus",
    "page-hall-first-buckeye-in-space-brutus" to "bricker-hall-pizza-chef-brutus",
    "bricker-hall-pizza-chef-brutus" to "journalism-building-mild-mannered-reporter-brutus",
    "ohio-union-brutus-buckeye" to "theatre-film-and-media-arts-building-break-a-leg-brutus",
    "gateway-super-hero-brutus" to "ohio-union-brutus-buckeye",
    "keenan-center-for-entrepreneurship-love-and-peace-brutus" to "university-square-north-brainy-brutus",
    "journalism-building-mild-mannered-reporter-brutus" to "college-traditions-dot-the-i-brutus",
    "college-traditions-dot-the-i-brutus" to "converse-hall-freedom-brutus",
    "the-blackwell-blackwell-brutus" to "schottenstein-center-basketball-brutus",
    "schottenstein-center-basketball-brutus" to "adventure-recreation-center-adventure-brutus",
    "adventure-recreation-center-adventure-brutus" to "blankenship-hall-public-safety-brutus",
    "longaberger-alumni-house-alumni-brutus" to "fawcett-center-gold-brutus",
)

/** One building and the statues inside it, as (statue name, location within the building). */
private fun site(
    name: String,
    address: String,
    latitude: Double,
    longitude: Double,
    accessNote: String,
    vararg statues: Pair<String, String>,
): List<Statue> = statues.map { (statueName, locationDescription) ->
    Statue(
        id = slug("$name $statueName"),
        name = statueName,
        siteName = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        locationDescription = locationDescription,
        accessNote = accessNote,
    )
}

/** "Wendy’s Dave Thomas Brutus" -> "wendys-dave-thomas-brutus". */
private fun slug(text: String): String =
    text.lowercase()
        .replace(Regex("[’']"), "")
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
