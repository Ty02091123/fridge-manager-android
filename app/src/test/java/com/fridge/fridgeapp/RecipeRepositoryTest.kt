package com.fridge.fridgeapp

import com.fridge.fridgeapp.database.Food
import com.fridge.fridgeapp.recipe.RecipeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeRepositoryTest {
    @Test
    fun recommendsRecipeWithAllRequiredIngredients() {
        val foods = listOf(
            food("西红柿", "2999-12-31"),
            food("鸡蛋", "2999-12-31")
        )

        val recommendations = RecipeRepository.recommend(foods)
        val tomatoEgg = recommendations.first { it.recipe.name == "番茄炒鸡蛋" }

        assertTrue(tomatoEgg.missingIngredients.isEmpty())
        assertTrue(tomatoEgg.score > 0.9)
    }

    @Test
    fun aliasesAreMatched() {
        val recommendations = RecipeRepository.recommend(
            listOf(food("马铃薯", "2999-12-31"), food("柿子椒", "2999-12-31"))
        )

        val potatoRecipe = recommendations.first { it.recipe.name == "青椒土豆丝" }
        assertEquals(listOf("青椒", "土豆"), potatoRecipe.matchedIngredients)
    }

    @Test
    fun expiringIngredientIsMarkedForPriority() {
        val recommendations = RecipeRepository.recommend(
            listOf(food("鸡蛋", "2000-01-01"))
        )

        assertTrue(recommendations.first { it.recipe.id == 3 }.usesExpiringFood)
    }

    @Test
    fun emptyInventoryHasNoRecommendations() {
        assertTrue(RecipeRepository.recommend(emptyList()).isEmpty())
    }

    private fun food(name: String, expireDate: String) = Food(
        name = name,
        type = "测试",
        count = 1,
        expireDate = expireDate
    )
}
