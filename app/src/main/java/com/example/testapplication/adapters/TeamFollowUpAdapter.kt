
package com.example.testapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.databinding.ItemTeamFollowUpBinding
import com.example.testapplication.datamodels.TeamFollowUp

class TeamFollowUpAdapter(
    private val onItemClick: (TeamFollowUp) -> Unit
) : ListAdapter<TeamFollowUp, TeamFollowUpAdapter.TeamViewHolder>(
    DiffCallback()
) {

    inner class TeamViewHolder(
        private val binding: ItemTeamFollowUpBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TeamFollowUp) {

            binding.tvConsultantName.text =
                item.consultantName

            binding.tvCallBackCount.text =
                String.format("%02d", item.callBacks)

            binding.tvShowroomVisitCount.text =
                String.format("%02d", item.showroomVisits)

            binding.layoutTeamFollowUpRow.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeamViewHolder {

        val binding = ItemTeamFollowUpBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TeamViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<TeamFollowUp>() {

        override fun areItemsTheSame(
            oldItem: TeamFollowUp,
            newItem: TeamFollowUp
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TeamFollowUp,
            newItem: TeamFollowUp
        ): Boolean {
            return oldItem == newItem
        }
    }
}
