package com.example.bolt2brutus.ui.leaderboard

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolt2brutus.R
import com.example.bolt2brutus.model.Race
import com.example.bolt2brutus.ui.formatElapsed
import com.example.bolt2brutus.viewmodel.LeaderboardState
import com.example.bolt2brutus.viewmodel.LeaderboardViewModel
import com.example.bolt2brutus.viewmodel.RunnerStats
import com.example.bolt2brutus.viewmodel.TimeComparison
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.abs

/** Public leaderboard for a chosen race. Tapping a runner compares their times with yours. */
class LeaderboardFragment : Fragment(R.layout.fragment_leaderboard) {

    private val leaderboardViewModel: LeaderboardViewModel by viewModels()

    private var races: List<Race> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindRacePicker(view.findViewById(R.id.raceSpinner))

        val myStandingText = view.findViewById<TextView>(R.id.myStandingText)
        val emptyText = view.findViewById<TextView>(R.id.emptyText)
        val adapter = LeaderboardAdapter(onClick = leaderboardViewModel::compareTimes)
        view.findViewById<RecyclerView>(R.id.leaderboardRecyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        leaderboardViewModel.state.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.entries)
            emptyText.isVisible = state.entries.isEmpty()
            myStandingText.text = describeStanding(state)
        }

        leaderboardViewModel.comparison.observe(viewLifecycleOwner) { comparison ->
            if (comparison != null) {
                showComparison(comparison)
                leaderboardViewModel.comparisonShown()
            }
        }

        leaderboardViewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                leaderboardViewModel.messageShown()
            }
        }
    }

    private fun bindRacePicker(raceSpinner: Spinner) {
        val spinnerAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, mutableListOf()
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        raceSpinner.adapter = spinnerAdapter

        raceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                races.getOrNull(position)?.let(leaderboardViewModel::selectRace)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        leaderboardViewModel.races.observe(viewLifecycleOwner) { list ->
            races = list
            spinnerAdapter.clear()
            spinnerAdapter.addAll(list.map { it.raceName })
            val selected = list.indexOfFirst { it.id == leaderboardViewModel.selectedRaceId }
            raceSpinner.setSelection(selected.coerceAtLeast(0))
            // setSelection does not fire the listener when the position is unchanged.
            list.getOrNull(selected.coerceAtLeast(0))?.let(leaderboardViewModel::selectRace)
        }
    }

    private fun describeStanding(state: LeaderboardState): String {
        val best = state.myBestMs ?: return getString(R.string.leaderboard_no_time)
        val rank = state.myRank
        return if (rank != null) {
            getString(R.string.leaderboard_my_rank, formatElapsed(best), rank)
        } else {
            getString(R.string.leaderboard_my_unranked, formatElapsed(best), LeaderboardViewModel.TOP_RUNNERS)
        }
    }

    private fun showComparison(comparison: TimeComparison) {
        val other = comparison.otherUsername
        val message = buildString {
            appendSection(R.string.compare_best, other, comparison.me.bestMs, comparison.other.bestMs)
            append("\n\n")
            appendSection(R.string.compare_average, other, comparison.me.averageMs, comparison.other.averageMs)
            append("\n\n")
            append(getString(R.string.compare_runs)).append('\n')
            append(getString(R.string.compare_line, getString(R.string.compare_you), comparison.me.runs.toString()))
            append('\n')
            append(getString(R.string.compare_line, other, comparison.other.runs.toString()))
            append("\n\n")
            append(verdict(other, comparison.me, comparison.other))
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.compare_title, other))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun StringBuilder.appendSection(heading: Int, other: String, mine: Long?, theirs: Long?) {
        append(getString(heading)).append('\n')
        append(getString(R.string.compare_line, getString(R.string.compare_you), mine.formatOrDash()))
        append('\n')
        append(getString(R.string.compare_line, other, theirs.formatOrDash()))
    }

    private fun Long?.formatOrDash(): String = this?.let(::formatElapsed) ?: getString(R.string.compare_no_time)

    private fun verdict(other: String, me: RunnerStats, them: RunnerStats): String {
        val mine = me.bestMs ?: return getString(R.string.compare_finish_first)
        val theirs = them.bestMs ?: return ""
        val gap = formatElapsed(abs(mine - theirs))
        return when {
            mine < theirs -> getString(R.string.compare_you_faster, gap)
            mine > theirs -> getString(R.string.compare_they_faster, other, gap)
            else -> getString(R.string.compare_tied)
        }
    }
}
