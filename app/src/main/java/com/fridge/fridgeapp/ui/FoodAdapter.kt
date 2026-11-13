package com.fridge.fridgeapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fridge.fridgeapp.database.Food
import com.fridge.fridgeapp.databinding.ItemFoodBinding

class FoodAdapter : ListAdapter<Food, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            binding.tvName.text = food.name
            binding.tvType.text = food.type
            binding.tvCount.text = "数量：${food.count}"
            binding.tvExpire.text = "过期：${food.expireDate}"
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
