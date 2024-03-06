package com.example.realestatemanager.ui.estate.add.media

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.EstatePreviewMediaItemBinding
import com.example.realestatemanager.databinding.MediaItemBinding
import com.example.realestatemanager.ui.estate.detail.medialist.MediaListAdapter

class MediaPreviewListAdapter :
    ListAdapter<MediaPreviewViewState, MediaPreviewListAdapter.MediaPreviewViewHolder>(
        MediaPreviewDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaPreviewViewHolder =
        MediaPreviewViewHolder.create(parent)

    override fun onBindViewHolder(holder: MediaPreviewViewHolder, position: Int) = holder.bind(getItem(position))


    class MediaPreviewViewHolder(private val binding: EstatePreviewMediaItemBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun create(parent: ViewGroup) = MediaPreviewViewHolder(
                binding = EstatePreviewMediaItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        fun bind(item: MediaPreviewViewState) {
            Glide
                .with(itemView.context)
                .load(item.uri)
                .transform(CenterCrop(), RoundedCorners(16))
                .error(R.drawable.baseline_add_home_24)
                .into(binding.mediaPreviewIv)

            binding.mediaPreviewFeaturedIv.setOnClickListener {
                item.onFeaturedEvent.invoke(!item.isFeatured)
            }

            if (item.type == "vid") {
                binding.mediaTypeIv.setImageResource(R.drawable.baseline_videocam_24)
                binding.mediaPreviewFeaturedIv.isVisible = false
            } else {
                binding.mediaTypeIv.setImageResource(R.drawable.outline_photo_camera_24)
            }

            binding.mediaPreviewFeaturedIv.setImageResource(
                if (item.isFeatured) R.drawable.baseline_star_24
                else R.drawable.baseline_star_border_24
            )

            binding.mediaPreviewDeleteIv.setOnClickListener { item.onDeleteEvent.invoke() }

            binding.mediaPreviewTitleTiet.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    item.onDescriptionChanged.invoke(binding.mediaPreviewTitleTiet.text.toString())

                }
            }

            binding.mediaPreviewTitleTiet.setText(item.description)
        }
    }
}

object MediaPreviewDiffCallback : DiffUtil.ItemCallback<MediaPreviewViewState>() {
    override fun areItemsTheSame(oldItem: MediaPreviewViewState, newItem: MediaPreviewViewState): Boolean =
        oldItem.id == newItem.id


    override fun areContentsTheSame(oldItem: MediaPreviewViewState, newItem: MediaPreviewViewState): Boolean =
        oldItem == newItem
}