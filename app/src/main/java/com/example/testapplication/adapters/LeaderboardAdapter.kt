
package com.example.testapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.datamodels.LeaderboardItem

class LeaderboardAdapter :
    ListAdapter<LeaderboardItem, LeaderboardAdapter.LeaderboardViewHolder>(
        DiffCallback()
    ) {

    inner class LeaderboardViewHolder(
        private val binding: ItemLeaderboardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeaderboardItem) {

            binding.tvLeaderboardConsultantName.text =
                item.consultantName

            binding.tvLeaderboardFollowUps.text =
                String.format("%02d", item.followUps)

            binding.tvLeaderboardInvoiceCount.text =
                String.format("%02d", item.invoiceConfirmed)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeaderboardViewHolder {

        val binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LeaderboardViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<LeaderboardItem>() {

        override fun areItemsTheSame(
            oldItem: LeaderboardItem,
            newItem: LeaderboardItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LeaderboardItem,
            newItem: LeaderboardItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
