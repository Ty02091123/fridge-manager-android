package com.fridge.fridgeapp.recipe

import com.fridge.fridgeapp.database.Food
import com.fridge.fridgeapp.util.ExpiryUtils

object RecipeRepository {
    private fun ingredient(name: String, vararg aliases: String) =
        IngredientRequirement(name, listOf(name) + aliases)

    private val recipes = listOf(
        Recipe(
            id = 1,
            name = "番茄炒鸡蛋",
            requiredIngredients = listOf(
                ingredient("番茄", "西红柿"),
                ingredient("鸡蛋", "蛋")
            ),
            optionalIngredients = listOf(ingredient("葱", "小葱")),
            steps = "1. 番茄切块，鸡蛋打散。\n2. 热锅加油，先把鸡蛋炒至凝固后盛出。\n3. 下番茄炒软，再倒回鸡蛋，加盐调味即可。"
        ),
        Recipe(
            id = 2,
            name = "青椒土豆丝",
            requiredIngredients = listOf(
                ingredient("青椒", "柿子椒"),
                ingredient("土豆", "马铃薯")
            ),
            optionalIngredients = listOf(ingredient("醋", "陈醋")),
            steps = "1. 土豆切丝后用清水冲去淀粉，青椒切丝。\n2. 热锅下油，先炒土豆丝至断生。\n3. 加入青椒和盐，快速翻炒，出锅前可淋少量醋。"
        ),
        Recipe(
            id = 3,
            name = "鸡蛋羹",
            requiredIngredients = listOf(ingredient("鸡蛋", "蛋")),
            optionalIngredients = listOf(ingredient("葱", "小葱"), ingredient("香油")),
            steps = "1. 鸡蛋打散，加入约 1.5 倍温水并搅匀。\n2. 过滤蛋液后盖住容器，水开上锅蒸 10 分钟。\n3. 出锅加少量生抽和香油。"
        ),
        Recipe(
            id = 4,
            name = "苹果酸奶杯",
            requiredIngredients = listOf(
                ingredient("苹果"),
                ingredient("酸奶", "牛奶")
            ),
            optionalIngredients = listOf(ingredient("燕麦")),
            steps = "1. 苹果洗净切小丁。\n2. 杯底放酸奶，再铺苹果丁。\n3. 可加入燕麦并冷藏十分钟，口感更好。"
        ),
        Recipe(
            id = 5,
            name = "猪肉炒白菜",
            requiredIngredients = listOf(
                ingredient("猪肉", "肉"),
                ingredient("白菜", "青菜")
            ),
            optionalIngredients = listOf(ingredient("蒜")),
            steps = "1. 猪肉切薄片，白菜切片。\n2. 先炒肉片至变色，加入蒜末增香。\n3. 放入白菜大火翻炒，加盐和少量生抽调味。"
        ),
        Recipe(
            id = 6,
            name = "虾仁炒蛋",
            requiredIngredients = listOf(
                ingredient("虾仁", "虾"),
                ingredient("鸡蛋", "蛋")
            ),
            optionalIngredients = listOf(ingredient("葱", "小葱")),
            steps = "1. 虾仁加少量盐腌制，鸡蛋打散。\n2. 先将虾仁炒至变色，再倒入蛋液。\n3. 轻推至蛋液凝固，撒葱花即可。"
        ),
        Recipe(
            id = 7,
            name = "番茄鸡蛋面",
            requiredIngredients = listOf(
                ingredient("番茄", "西红柿"),
                ingredient("鸡蛋", "蛋"),
                ingredient("面条", "面")
            ),
            optionalIngredients = listOf(ingredient("青菜")),
            steps = "1. 番茄切块炒出汤汁，加入清水煮开。\n2. 放入面条煮至八成熟。\n3. 淋入蛋液，加青菜和盐煮熟即可。"
        ),
        Recipe(
            id = 8,
            name = "牛奶燕麦粥",
            requiredIngredients = listOf(
                ingredient("牛奶", "奶"),
                ingredient("燕麦")
            ),
            optionalIngredients = listOf(ingredient("香蕉", "苹果")),
            steps = "1. 燕麦加少量清水煮开。\n2. 倒入牛奶小火搅拌，避免糊底。\n3. 煮至浓稠后可加入水果丁。"
        ),
        Recipe(
            id = 9,
            name = "香蕉牛奶",
            requiredIngredients = listOf(
                ingredient("香蕉"),
                ingredient("牛奶", "奶")
            ),
            optionalIngredients = listOf(ingredient("蜂蜜")),
            steps = "1. 香蕉去皮切段。\n2. 与牛奶一起放入搅拌机。\n3. 搅打均匀，可按口味加少量蜂蜜。"
        ),
        Recipe(
            id = 10,
            name = "青菜鸡蛋汤",
            requiredIngredients = listOf(
                ingredient("青菜", "菠菜"),
                ingredient("鸡蛋", "蛋")
            ),
            optionalIngredients = listOf(ingredient("香油")),
            steps = "1. 锅中加水烧开，放入洗净的青菜。\n2. 缓慢淋入打散的蛋液。\n3. 加盐和香油后立即关火。"
        ),
        Recipe(
            id = 11,
            name = "蒜蓉炒青菜",
            requiredIngredients = listOf(
                ingredient("青菜", "菠菜", "白菜"),
                ingredient("蒜")
            ),
            optionalIngredients = listOf(ingredient("蚝油")),
            steps = "1. 青菜洗净沥干，蒜切末。\n2. 热锅下油，炒香蒜末。\n3. 加入青菜大火快炒，加盐调味后出锅。"
        ),
        Recipe(
            id = 12,
            name = "牛肉蔬菜饭",
            requiredIngredients = listOf(
                ingredient("牛肉", "肉"),
                ingredient("米饭", "米"),
                ingredient("青菜", "白菜")
            ),
            optionalIngredients = listOf(ingredient("胡萝卜")),
            steps = "1. 牛肉切片腌制，蔬菜切小。\n2. 先炒牛肉，再放入蔬菜炒熟。\n3. 加入米饭翻匀，加生抽调味。"
        )
    )

    fun recommend(foods: List<Food>): List<RecipeRecommendation> {
        val availableFoods = foods.filter { it.count > 0 }
        return recipes.mapNotNull { recipe ->
            val matchedRequirements = recipe.requiredIngredients.filter { requirement ->
                availableFoods.any { matches(requirement, it.name) }
            }
            if (matchedRequirements.isEmpty()) return@mapNotNull null

            val matchedFoods = availableFoods.filter { food ->
                recipe.requiredIngredients.any { matches(it, food.name) }
            }
            val missing = recipe.requiredIngredients
                .filterNot { it in matchedRequirements }
                .map { it.name }
            val optionalMatches = recipe.optionalIngredients.count { requirement ->
                availableFoods.any { matches(requirement, it.name) }
            }
            val coverage = matchedRequirements.size.toDouble() /
                recipe.requiredIngredients.size
            val usesExpiring = matchedFoods.any {
                ExpiryUtils.isExpiringSoon(it.expireDate) || ExpiryUtils.isExpired(it.expireDate)
            }
            val score = (
                coverage +
                    optionalMatches * 0.03 +
                    if (usesExpiring) 0.12 else 0.0
                ).coerceAtMost(1.0)

            RecipeRecommendation(
                recipe = recipe,
                matchedIngredients = matchedRequirements.map { it.name },
                missingIngredients = missing,
                score = score,
                usesExpiringFood = usesExpiring
            )
        }.sortedWith(
            compareByDescending<RecipeRecommendation> { it.missingIngredients.isEmpty() }
                .thenByDescending { it.score }
                .thenByDescending { it.matchedIngredients.size }
        )
    }

    private fun matches(requirement: IngredientRequirement, foodName: String): Boolean {
        val normalizedFood = foodName.trim().lowercase()
        return requirement.aliases.any { alias ->
            val normalizedAlias = alias.trim().lowercase()
            normalizedFood.contains(normalizedAlias) || normalizedAlias.contains(normalizedFood)
        }
    }
}
