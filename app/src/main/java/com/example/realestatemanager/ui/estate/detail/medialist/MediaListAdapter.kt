package com.example.realestatemanager.ui.estate.detail.medialist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.MediaItemBinding
import com.example.realestatemanager.ui.utils.NativePhoto.Companion.load

class MediaListAdapter :
    ListAdapter<MediaViewState, MediaListAdapter.MediaViewHolder>(MediaDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder =
        MediaViewHolder.create(parent)

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) = holder.bind(getItem(position))

    class MediaViewHolder(private val binding: MediaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup) = MediaViewHolder(
                binding = MediaItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        fun bind(item: MediaViewState) {
            item.mediaUri
                .load(binding.mediaIv)
                .transform(CenterCrop())
                .error(R.drawable.baseline_add_home_24)
                .into(binding.mediaIv)

            if (item.description.isNullOrBlank()) {
                binding.mediaTitleTv.isVisible = false
            } else {
                binding.mediaTitleTv.text = item.description
                binding.mediaTitleTv.contentDescription = item.description
            }

            if (item.type == "vid") {
                binding.mediaTypeIv.setImageResource(R.drawable.baseline_videocam_24)
            } else {
                binding.mediaTypeIv.setImageResource(R.drawable.outline_photo_camera_24)
            }
        }
    }
}

object MediaDiffCallback : DiffUtil.ItemCallback<MediaViewState>() {
    override fun areItemsTheSame(oldItem: MediaViewState, newItem: MediaViewState): Boolean =
        oldItem.mediaUri == newItem.mediaUri && oldItem.description == newItem.description


    override fun areContentsTheSame(oldItem: MediaViewState, newItem: MediaViewState): Boolean =
        oldItem == newItem
}
