package com.fridge.fridgeapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fridge.fridgeapp.R
import com.fridge.fridgeapp.databinding.ItemRecipeBinding
import com.fridge.fridgeapp.recipe.Recipe
import com.fridge.fridgeapp.recipe.RecipeRecommendation
import kotlin.math.roundToInt

class RecipeAdapter(
    private val onViewSteps: (Recipe) -> Unit
) : ListAdapter<RecipeRecommendation, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recommendation: RecipeRecommendation) {
            val context = binding.root.context
            binding.tvRecipeName.text = recommendation.recipe.name
            binding.tvMatch.text = context.getString(
                R.string.recipe_match,
                (recommendation.score * 100).roundToInt()
            )
            binding.tvIngredients.text = context.getString(
                R.string.recipe_available_ingredients,
                recommendation.matchedIngredients.joinToString("、")
            )
            binding.tvMissing.text = if (recommendation.missingIngredients.isEmpty()) {
                context.getString(R.string.recipe_all_ingredients_ready)
            } else {
                context.getString(
                    R.string.recipe_missing_ingredients,
                    recommendation.missingIngredients.joinToString("、")
                )
            }
            binding.tvExpiringBoost.visibility =
                if (recommendation.usesExpiringFood) View.VISIBLE else View.GONE
            binding.btnSteps.setOnClickListener { onViewSteps(recommendation.recipe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<RecipeRecommendation>() {
        override fun areItemsTheSame(
            oldItem: RecipeRecommendation,
            newItem: RecipeRecommendation
        ) = oldItem.recipe.id == newItem.recipe.id

        override fun areContentsTheSame(
            oldItem: RecipeRecommendation,
            newItem: RecipeRecommendation
        ) = oldItem == newItem
    }
}
