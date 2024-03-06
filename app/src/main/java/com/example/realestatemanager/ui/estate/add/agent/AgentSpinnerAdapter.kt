package com.example.realestatemanager.ui.estate.add.agent

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ListAdapter
import com.example.realestatemanager.databinding.EstateAddSpinnerItemBinding

class AgentSpinnerAdapter : ListAdapter, Filterable {
    private val dataSetObservable = DataSetObservable()
    private var items = emptyList<AgentViewStateItem>()


    override fun registerDataSetObserver(observer: DataSetObserver?) {
        dataSetObservable.registerObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        dataSetObservable.unregisterObserver(observer)
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): AgentViewStateItem? = items.getOrNull(position)

    override fun getItemId(position: Int): Long = getItem(position)?.id ?: -1L

    override fun hasStableIds(): Boolean = true

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView != null) {
            EstateAddSpinnerItemBinding.bind(convertView)
        } else {
            EstateAddSpinnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        getItem(position)?.let { item ->
            binding.formSpinnerItemTvName.text = item.name
        }
        return binding.root
    }

    override fun getItemViewType(position: Int): Int = 0

    override fun getViewTypeCount(): Int = 1

    override fun isEmpty(): Boolean = count == 0

    override fun areAllItemsEnabled(): Boolean = true

    override fun isEnabled(position: Int): Boolean = true

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence) = FilterResults()
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) = Unit
        override fun convertResultToString(resultValue: Any): CharSequence =
            (resultValue as AgentViewStateItem).name
    }

    fun setData(items: List<AgentViewStateItem>) {
        this.items = items
        dataSetObservable.notifyChanged()
    }
}