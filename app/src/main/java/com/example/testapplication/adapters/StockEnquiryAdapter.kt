package com.example.testapplication.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.datamodels.StockEnquiry

class StockEnquiryAdapter :
    ListAdapter<
            StockEnquiry,
            StockEnquiryAdapter.EnquiryViewHolder
            >(DiffCallback()) {

    inner class EnquiryViewHolder(
        private val binding: ItemStockEnquiryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StockEnquiry) {

            binding.tvEnquiryCustomerName.text =
                item.customerName

            binding.tvEnquiryMobile.text =
                "+91 ${item.mobileNumber}"

            binding.tvEnquiryCertified.text =
                if (item.isCertified) {
                    "Certified"
                } else {
                    "Not Certified"
                }

            binding.tvEnquiryCertified.setTextColor(
                if (item.isCertified) {
                    Color.rgb(117, 185, 0)
                } else {
                    Color.GRAY
                }
            )

            binding.tvLastEnquiredDate.text =
                item.lastEnquiredOn
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EnquiryViewHolder {

        val binding = ItemStockEnquiryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return EnquiryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: EnquiryViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback :
        DiffUtil.ItemCallback<StockEnquiry>() {

        override fun areItemsTheSame(
            oldItem: StockEnquiry,
            newItem: StockEnquiry
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: StockEnquiry,
            newItem: StockEnquiry
        ): Boolean {
            return oldItem == newItem
        }
    }
}