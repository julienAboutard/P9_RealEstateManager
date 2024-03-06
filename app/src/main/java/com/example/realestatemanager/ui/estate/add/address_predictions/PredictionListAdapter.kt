package com.example.realestatemanager.ui.estate.add.address_predictions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.EstateAddressPredictionItemBinding

class PredictionListAdapter :
    ListAdapter<PredictionViewState, PredictionListAdapter.PredictionViewHolder>(PredictionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        return PredictionViewHolder(
            EstateAddressPredictionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    class PredictionViewHolder(private var binding: EstateAddressPredictionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PredictionViewState) {

            binding.predictionItemCardview.setOnClickListener {
                item.onClickEvent.invoke(item.address)
            }
            binding.formSuggestionTextView.text = item.address
        }
    }

}

object PredictionDiffCallback : DiffUtil.ItemCallback<PredictionViewState>() {
    override fun areItemsTheSame(oldItem: PredictionViewState, newItem: PredictionViewState): Boolean {
        return oldItem.address == newItem.address
    }


    override fun areContentsTheSame(oldItem: PredictionViewState, newItem: PredictionViewState): Boolean =
        oldItem == newItem
}