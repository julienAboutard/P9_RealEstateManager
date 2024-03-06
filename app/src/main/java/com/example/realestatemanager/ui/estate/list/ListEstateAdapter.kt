/*
 * Copyright (c) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.realestatemanager.ui.estate.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.EstateItemBinding
import com.example.realestatemanager.ui.utils.NativePhoto.Companion.load

class ListEstateAdapter(private val onItemClicked: (ListEstateViewState) -> Unit) :
    ListAdapter<ListEstateViewState, ListEstateAdapter.ListViewHolder>(DiffCallback) {

    private lateinit var context: Context

    class ListViewHolder(private var binding: EstateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListEstateViewState) {
            //binding.root.setOnClickListener { item.onClickEvent.invoke() }
            binding.estateItemTypeTv.text = binding.root.context.resources.getString(item.estateType)
            binding.estateItemPriceTv.text = item.price
            binding.estateItemSurfaceTv.text = item.surface
            binding.estateItemLocationTv.text = item.location
            binding.estateItemRoomTv.text = item.room.toCharSequence(binding.root.context)
            binding.estateItemBedroomTv.text = item.bedroom.toCharSequence(binding.root.context)
            binding.estateItemBathroomTv.text = item.bathroom.toCharSequence(binding.root.context)
            when (item.isSold) {
                true -> {
                    binding.estateItemCornerBannerIv.visibility = View.VISIBLE
                    binding.estateItemSoldTv.visibility = View.VISIBLE
                }
                else -> {
                    binding.estateItemCornerBannerIv.visibility = View.GONE
                    binding.estateItemSoldTv.visibility = View.GONE
                }
            }
            item.featuredPicture
                .load(binding.estateItemPhotoIv)
                .transform(CenterCrop(), RoundedCorners(16))
                .error(R.drawable.baseline_add_home_24)
                .into(binding.estateItemPhotoIv)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        context = parent.context
        return ListViewHolder(
            EstateItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ListEstateViewState>() {
            override fun areItemsTheSame(oldItem: ListEstateViewState, newItem: ListEstateViewState): Boolean {
                return (oldItem.id == newItem.id ||
                        oldItem.estateType == newItem.estateType)
            }

            override fun areContentsTheSame(oldItem: ListEstateViewState, newItem: ListEstateViewState): Boolean {
                return oldItem == newItem
            }
        }
    }
}
