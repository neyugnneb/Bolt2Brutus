package com.example.bolt2brutus.ui.leaderboard

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bolt2brutus.R
import com.example.bolt2brutus.ui.formatElapsed
import com.example.bolt2brutus.viewmodel.LeaderboardEntry

/** Lists each runner's best time. The signed-in user's row is bold. */
class LeaderboardAdapter(
    private val onClick: (LeaderboardEntry) -> Unit,
) : ListAdapter<LeaderboardEntry, LeaderboardAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rank: TextView = view.findViewById(R.id.rankText)
        val username: TextView = view.findViewById(R.id.usernameText)
        val time: TextView = view.findViewById(R.id.timeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard_entry, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        val context = holder.itemView.context
        holder.rank.text = context.getString(R.string.leaderboard_rank, entry.rank)
        holder.username.text = if (entry.isMe) {
            context.getString(R.string.leaderboard_you, entry.username)
        } else {
            entry.username
        }
        holder.time.text = formatElapsed(entry.bestMs)
        val style = if (entry.isMe) Typeface.BOLD else Typeface.NORMAL
        listOf(holder.rank, holder.username, holder.time).forEach { it.setTypeface(null, style) }
        holder.itemView.setOnClickListener { onClick(entry) }
    }

    private object DiffCallback : DiffUtil.ItemCallback<LeaderboardEntry>() {
        override fun areItemsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry) =
            oldItem.userId == newItem.userId
        override fun areContentsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry) = oldItem == newItem
    }
}
