package com.fridge.fridgeapp.recipe

data class IngredientRequirement(
    val name: String,
    val aliases: List<String>
)

data class Recipe(
    val id: Int,
    val name: String,
    val requiredIngredients: List<IngredientRequirement>,
    val optionalIngredients: List<IngredientRequirement>,
    val steps: String
)

data class RecipeRecommendation(
    val recipe: Recipe,
    val matchedIngredients: List<String>,
    val missingIngredients: List<String>,
    val score: Double,
    val usesExpiringFood: Boolean
)
