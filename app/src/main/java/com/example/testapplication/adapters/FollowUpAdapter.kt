package com.example.testapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.databinding.ItemFollowupBinding
import com.example.testapplication.datamodels.FollowUp

class FollowUpAdapter :
    ListAdapter<FollowUp, FollowUpAdapter.FollowUpViewHolder>(DiffCallback()) {

    inner class FollowUpViewHolder(
        private val binding: ItemFollowupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FollowUp) {

            binding.tvCreatedDateTime.text =
                "${item.createdDate}, ${item.createdTime}"

            binding.tvFollowUpType.text =
                item.followUpType

            binding.tvRemarks.text =
                item.remarks

            binding.tvNextFollowUpType.text =
                item.nextFollowUpType

            binding.tvNextFollowUpDateTime.text =
                "${item.followUpType}, ${item.nextFollowUpDate}\n${item.nextFollowUpTime}"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowUpViewHolder {

        val binding = ItemFollowupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FollowUpViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FollowUpViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<FollowUp>() {

        override fun areItemsTheSame(
            oldItem: FollowUp,
            newItem: FollowUp
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: FollowUp,
            newItem: FollowUp
        ): Boolean = oldItem == newItem
    }
}