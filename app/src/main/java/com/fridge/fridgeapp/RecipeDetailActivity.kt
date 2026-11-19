package com.fridge.fridgeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fridge.fridgeapp.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.title = intent.getStringExtra(EXTRA_NAME).orEmpty()
        binding.tvMatch.text = getString(
            R.string.recipe_detail_match,
            intent.getIntExtra(EXTRA_MATCH, 0)
        )
        binding.tvIngredients.text = getString(
            R.string.recipe_detail_ingredients,
            intent.getStringExtra(EXTRA_INGREDIENTS).orEmpty()
        )
        binding.tvMissing.text = intent.getStringExtra(EXTRA_MISSING).orEmpty()
        binding.tvSteps.text = intent.getStringExtra(EXTRA_STEPS).orEmpty()
    }

    companion object {
        const val EXTRA_NAME = "recipe_name"
        const val EXTRA_MATCH = "recipe_match"
        const val EXTRA_INGREDIENTS = "recipe_ingredients"
        const val EXTRA_MISSING = "recipe_missing"
        const val EXTRA_STEPS = "recipe_steps"
    }
}
