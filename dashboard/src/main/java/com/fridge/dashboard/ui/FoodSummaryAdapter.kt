package com.fridge.dashboard.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fridge.dashboard.R
import com.fridge.dashboard.data.DashboardDateUtils
import com.fridge.dashboard.data.FoodItem
import com.fridge.dashboard.databinding.ItemFoodSummaryBinding

class FoodSummaryAdapter :
    ListAdapter<FoodItem, FoodSummaryAdapter.FoodViewHolder>(FoodDiffCallback()) {

    inner class FoodViewHolder(private val binding: ItemFoodSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: FoodItem) {
            val context = binding.root.context
            binding.tvName.text = food.name
            binding.tvMeta.text = context.getString(
                R.string.food_meta,
                food.type,
                food.count,
                food.expireDate
            )
            val days = DashboardDateUtils.daysUntil(food.expireDate)
            val (status, color) = when {
                days == null -> context.getString(R.string.status_unknown) to R.color.status_unknown
                days < 0 -> context.getString(R.string.status_expired) to R.color.status_expired
                days <= 7 -> context.getString(R.string.status_expiring, days) to R.color.status_expiring
                else -> context.getString(R.string.status_fresh) to R.color.status_fresh
            }
            binding.tvStatus.text = status
            binding.tvStatus.setTextColor(ContextCompat.getColor(context, color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FoodDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem) =
            oldItem == newItem
    }
}
