package com.example.bolt2brutus.data.seed

import com.example.bolt2brutus.data.FirestoreCollections
import com.example.bolt2brutus.data.repository.RaceAttemptRepository
import com.example.bolt2brutus.data.repository.RaceRepository
import com.example.bolt2brutus.data.repository.UserRepository
import com.example.bolt2brutus.model.Race
import com.example.bolt2brutus.model.RaceAttempt
import com.example.bolt2brutus.model.StatueRef
import com.example.bolt2brutus.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Debug-only test data for the leaderboard.
 *
 * Security rules only let a user write their own attempts, so each fake runner is a real
 * Firebase Auth account. They sign in through a second [FirebaseApp] so the person using
 * the app stays signed in. [remove] deletes the runners' accounts, profiles, and attempts.
 */
class TestLeaderboardData {

    private val testApp: FirebaseApp = FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext)
        .find { it.name == APP_NAME }
        ?: FirebaseApp.initializeApp(
            FirebaseApp.getInstance().applicationContext,
            FirebaseApp.getInstance().options,
            APP_NAME,
        )
    private val testAuth = FirebaseAuth.getInstance(testApp)
    private val testDb = FirebaseFirestore.getInstance(testApp)

    /**
     * Replaces any earlier test data with fresh attempts for every fake runner, plus a few
     * for the signed-in user, and returns the number of attempts written.
     */
    suspend fun add(me: User?): Int {
        val races = RaceRepository().getAll()
        check(races.isNotEmpty()) { "No races yet. Open the Races tab once so they get seeded." }
        val random = Random(SEED)
        var written = 0

        for ((index, username) in RUNNERS.withIndex()) {
            val uid = signInOrCreate(index)
            UserRepository(testDb).create(User(uid = uid, email = email(index), username = username))
            RaceAttemptRepository(testDb).deleteAllForUser(uid)
            val pace = random.nextDouble(2.4, 4.6)
            val attempts = races.filter { random.nextDouble() < 0.7 }
                .flatMap { race -> List(random.nextInt(1, 4)) { attempt(uid, username, race, pace, random) } }
            writeAll(testDb, attempts.map { testDb.collection(FirestoreCollections.RACE_ATTEMPTS).document() to it })
            written += attempts.size
            testAuth.signOut()
        }

        if (me != null) {
            // Fixed IDs so remove() can find the signed-in user's test attempts without a marker field.
            val db = FirebaseFirestore.getInstance()
            val attempts = races.take(MY_TEST_RACES).mapIndexed { index, race ->
                db.collection(FirestoreCollections.RACE_ATTEMPTS).document(myAttemptId(me.uid, index)) to
                    attempt(me.uid, me.username, race, pace = 3.2, random)
            }
            writeAll(db, attempts)
            written += attempts.size
        }
        return written
    }

    /** Deletes every fake runner and the signed-in user's test attempts. */
    suspend fun remove(me: User?) {
        for (index in RUNNERS.indices) {
            val uid = signInOrNull(index) ?: continue
            RaceAttemptRepository(testDb).deleteAllForUser(uid)
            UserRepository(testDb).delete(uid)
            testAuth.currentUser?.delete()?.await()
        }
        if (me != null) {
            // Security rules reject deleting a document that does not exist, so look them up first.
            val db = FirebaseFirestore.getInstance()
            val mine = db.collection(FirestoreCollections.RACE_ATTEMPTS)
                .whereEqualTo("userId", me.uid).get().await()
                .documents.filter { it.id.startsWith(myAttemptId(me.uid, index = null)) }
            if (mine.isNotEmpty()) {
                db.runBatch { batch -> mine.forEach { batch.delete(it.reference) } }.await()
            }
        }
    }

    private suspend fun signInOrCreate(index: Int): String =
        signInOrNull(index)
            ?: testAuth.createUserWithEmailAndPassword(email(index), PASSWORD).await().user!!.uid

    private suspend fun signInOrNull(index: Int): String? =
        try {
            testAuth.signInWithEmailAndPassword(email(index), PASSWORD).await().user?.uid
        } catch (e: FirebaseAuthInvalidUserException) {
            null
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Newer Firebase projects report a missing account this way to prevent email enumeration.
            null
        }

    /** A completed attempt whose time follows from the statues' distance and the runner's [pace] in m/s. */
    private fun attempt(uid: String, username: String, race: Race, pace: Double, random: Random): RaceAttempt {
        val meters = distanceMeters(race.startStatue, race.endStatue) * ROUTE_DETOUR
        val elapsedMs = (meters / pace * 1000 * random.nextDouble(0.92, 1.12)).roundToLong()
        val start = System.currentTimeMillis() - random.nextLong(1, 30) * DAY_MS - random.nextLong(DAY_MS)
        return RaceAttempt(
            userId = uid,
            username = username,
            raceId = race.id,
            raceName = race.raceName,
            startTime = Timestamp(Date(start)),
            endTime = Timestamp(Date(start + elapsedMs)),
            elapsedTimeMs = elapsedMs,
            completed = true,
        )
    }

    private suspend fun writeAll(db: FirebaseFirestore, docs: List<Pair<DocumentReference, RaceAttempt>>) {
        if (docs.isEmpty()) return
        db.runBatch { batch -> docs.forEach { (ref, attempt) -> batch.set(ref, attempt) } }.await()
    }

    private fun distanceMeters(a: StatueRef, b: StatueRef): Double {
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val dLat = lat2 - lat1
        val dLng = Math.toRadians(b.longitude - a.longitude)
        val h = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLng / 2).pow(2)
        return 2 * EARTH_RADIUS_M * asin(sqrt(h))
    }

    private fun email(index: Int) = "test-runner-${index + 1}@bolt2brutus.test"

    /** With a null [index], the prefix shared by all of the user's test attempt IDs. */
    private fun myAttemptId(uid: String, index: Int?) = "test-$uid-${index ?: ""}"

    private companion object {
        const val APP_NAME = "test-leaderboard-data"
        const val PASSWORD = "test-runner-password"
        const val SEED = 5236
        const val MY_TEST_RACES = 4
        const val ROUTE_DETOUR = 1.3
        const val EARTH_RADIUS_M = 6_371_000.0
        const val DAY_MS = 24 * 60 * 60 * 1000L

        val RUNNERS = listOf(
            "buckeye_bolt", "oval_sprinter", "mirror_lake_mike", "scarlet_streak",
            "gray_gazelle", "hayes_hustler", "south_campus_sam", "thompson_dash",
        )
    }
}
