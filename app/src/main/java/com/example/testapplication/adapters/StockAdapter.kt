
package com.example.testapplication.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.example.testapplication.R
import com.example.testapplication.databinding.ItemStockBinding
import com.example.testapplication.datamodels.Stock

class StockAdapter(
    private val onItemClick: (Stock) -> Unit
) : ListAdapter<Stock, StockAdapter.StockViewHolder>(DiffCallback()) {

    inner class StockViewHolder(
        private val binding: ItemStockBinding
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Stock) {

            binding.tvStockCarName.text = item.carName
            binding.tvStockNumber.text = item.stockNumber
            binding.tvAvailability.text = item.availability

            binding.tvPhotosUploaded.text =
                if (item.photosUploaded) {
                    "Photos uploaded"
                } else {
                    "Photos not uploaded"
                }

            binding.tvAvailability.setTextColor(
                if (item.availability.equals("In Stock", true)) {
                    Color.rgb(0, 143, 200)
                } else {
                    Color.BLACK
                }
            )

            // Load image from URL
            Glide.with(binding.root.context)
                .load(item.images.firstOrNull())
                .centerCrop()
                .into(binding.ivStockImage)

            binding.tvPhotoCount.text =
                String.format("%02d", item.photoCount)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StockViewHolder {

        val binding = ItemStockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: StockViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Stock>() {

        override fun areItemsTheSame(
            oldItem: Stock,
            newItem: Stock
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Stock,
            newItem: Stock
        ) = oldItem == newItem
    }
}
