package com.example.testapplication.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.R
import com.example.testapplication.databinding.ItemStockFullImageBinding

class StockImageAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<StockImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(
        private val binding: ItemStockFullImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageName: String) {

            val imageResource = binding.root.context.resources.getIdentifier(
                imageName,
                "drawable",
                binding.root.context.packageName
            )

            if (imageResource != 0) {

                binding.ivFullStockImage.setImageResource(imageResource)

            } else {

                binding.ivFullStockImage.setImageResource(
                    R.drawable.ic_launcher_background
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewHolder {

        val binding = ItemStockFullImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ImageViewHolder,
        position: Int
    ) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}