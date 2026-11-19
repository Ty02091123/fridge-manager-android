package com.fridge.fridgeapp

import android.os.Bundle
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    private val adapter = RecipeAdapter { recipe ->
        MaterialAlertDialogBuilder(this)
            .setTitle(recipe.name)
            .setMessage(recipe.steps)
            .setPositiveButton(R.string.got_it, null)
            .show()
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
