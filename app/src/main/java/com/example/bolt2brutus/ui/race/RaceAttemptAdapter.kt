package com.example.bolt2brutus.ui.race

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bolt2brutus.R
import com.example.bolt2brutus.model.RaceAttempt
import java.util.Locale

/** Lists race attempts with buttons to finish an in-progress attempt or delete one. */
class RaceAttemptAdapter(
    private val onFinish: (RaceAttempt) -> Unit,
    private val onDelete: (RaceAttempt) -> Unit,
) : ListAdapter<RaceAttempt, RaceAttemptAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val raceName: TextView = view.findViewById(R.id.raceNameText)
        val status: TextView = view.findViewById(R.id.statusText)
        val finishButton: Button = view.findViewById(R.id.finishButton)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_race_attempt, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attempt = getItem(position)
        holder.raceName.text = attempt.raceName
        holder.status.text = attempt.elapsedTimeMs
            ?.takeIf { attempt.completed }
            ?.let(::formatElapsed)
            ?: holder.itemView.context.getString(R.string.in_progress)
        holder.finishButton.visibility = if (attempt.completed) View.GONE else View.VISIBLE
        holder.finishButton.setOnClickListener { onFinish(attempt) }
        holder.deleteButton.setOnClickListener { onDelete(attempt) }
    }

    private fun formatElapsed(ms: Long): String {
        val minutes = ms / 60_000
        val seconds = (ms % 60_000) / 1000
        val hundredths = (ms % 1000) / 10
        return String.format(Locale.US, "%d:%02d.%02d", minutes, seconds, hundredths)
    }

    private object DiffCallback : DiffUtil.ItemCallback<RaceAttempt>() {
        override fun areItemsTheSame(oldItem: RaceAttempt, newItem: RaceAttempt) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RaceAttempt, newItem: RaceAttempt) = oldItem == newItem
    }
}
