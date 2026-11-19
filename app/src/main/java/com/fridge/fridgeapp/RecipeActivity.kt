package com.fridge.fridgeapp

import android.os.Bundle
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridge.fridgeapp.database.AppDatabase
import com.fridge.fridgeapp.databinding.ActivityRecipeBinding
import com.fridge.fridgeapp.recipe.RecipeRepository
import com.fridge.fridgeapp.ui.RecipeAdapter
import kotlinx.coroutines.launch

class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    private val adapter = RecipeAdapter { recommendation ->
        val recipe = recommendation.recipe
        startActivity(
            Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra(RecipeDetailActivity.EXTRA_NAME, recipe.name)
                putExtra(
                    RecipeDetailActivity.EXTRA_MATCH,
                    (recommendation.score * 100).toInt()
                )
                putExtra(
                    RecipeDetailActivity.EXTRA_INGREDIENTS,
                    getString(
                        R.string.recipe_available_ingredients,
                        recommendation.matchedIngredients.joinToString("、")
                    )
                )
                putExtra(
                    RecipeDetailActivity.EXTRA_MISSING,
                    if (recommendation.missingIngredients.isEmpty()) {
                        getString(R.string.recipe_all_ingredients_ready)
                    } else {
                        getString(
                            R.string.recipe_missing_ingredients,
                            recommendation.missingIngredients.joinToString("、")
                        )
                    }
                )
                putExtra(RecipeDetailActivity.EXTRA_STEPS, recipe.steps)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.rvRecipes.adapter = adapter
        binding.rvRecipes.layoutManager = LinearLayoutManager(this)

        val dao = AppDatabase.getInstance(this).foodDao()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dao.getAllFood().collect { foods ->
                    val recommendations = RecipeRepository.recommend(foods)
                    adapter.submitList(recommendations)
                    binding.emptyState.visibility =
                        if (recommendations.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }
}
