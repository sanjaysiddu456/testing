package com.example.testapplication.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.datamodels.Enquiry


class EnquiryAdapter(
    private val onItemClick: (Enquiry) -> Unit
) : ListAdapter<Enquiry, EnquiryAdapter.EnquiryViewHolder>(DiffCallback()) {

    inner class EnquiryViewHolder(
        private val binding: ItemEnquiryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(enquiry: Enquiry) {

            binding.tvCustomerName.text = enquiry.customerName
            binding.tvMobileNumber.text = enquiry.mobileNumber
            binding.tvCarName.text = enquiry.carName
            binding.tvCallBackIn.text = enquiry.callBackIn
            binding.tvCallBackDate.text =
                "${enquiry.callBackDate} | ${enquiry.callBackTime}"

            binding.tvCertified.text =
                if (enquiry.isCertified) "Certified" else "Not Certified"

            binding.root.setOnClickListener {
                onItemClick(enquiry)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EnquiryViewHolder {

        val binding = ItemEnquiryBinding.inflate(
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

    class DiffCallback : DiffUtil.ItemCallback<Enquiry>() {

        override fun areItemsTheSame(
            oldItem: Enquiry,
            newItem: Enquiry
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Enquiry,
            newItem: Enquiry
        ): Boolean {
            return oldItem == newItem
        }
    }
}