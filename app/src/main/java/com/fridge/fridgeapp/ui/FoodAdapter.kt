package com.fridge.fridgeapp.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fridge.fridgeapp.R
import com.fridge.fridgeapp.database.Food
import com.fridge.fridgeapp.databinding.ItemFoodBinding
import com.fridge.fridgeapp.util.ExpiryUtils

class FoodAdapter(
    private val onEdit: (Food) -> Unit,
    private val onDelete: (Food) -> Unit
) : ListAdapter<Food, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            val context = binding.root.context
            binding.tvName.text = food.name
            binding.tvType.text = food.type
            binding.tvCount.text = context.getString(R.string.quantity_format, food.count)
            binding.tvExpire.text = context.getString(R.string.expiry_date_format, food.expireDate)

            val days = ExpiryUtils.daysUntilExpiry(food.expireDate)
            val (label, textColor) = when {
                days == null -> context.getString(R.string.status_unknown) to R.color.status_unknown
                days < 0 -> context.getString(R.string.status_expired) to R.color.status_expired
                days <= 7 -> context.getString(R.string.status_expiring, days) to R.color.status_expiring
                else -> context.getString(R.string.status_fresh) to R.color.status_fresh
            }
            binding.tvStatus.text = label
            binding.tvStatus.setTextColor(ContextCompat.getColor(context, textColor))

            binding.root.setOnClickListener { onEdit(food) }
            binding.btnEdit.setOnClickListener { onEdit(food) }
            binding.btnDelete.setOnClickListener { onDelete(food) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class FoodDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}
