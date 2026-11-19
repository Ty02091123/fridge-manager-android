package com.fridge.dashboard.data

object InventoryCsvFormatter {
    fun build(foods: List<FoodItem>): String = buildString {
        append('\uFEFF')
        appendLine("编号,名称,分类,数量,过期日期")
        foods.forEach { food ->
            listOf(
                food.id.toString(),
                food.name,
                food.type,
                food.count.toString(),
                food.expireDate
            ).joinTo(this, separator = ",") { escape(it) }
            appendLine()
        }
    }

    private fun escape(value: String): String {
        val safeValue = if (value.startsWith("=") || value.startsWith("+") ||
            value.startsWith("-") || value.startsWith("@")
        ) {
            "'$value"
        } else {
            value
        }
        return "\"${safeValue.replace("\"", "\"\"")}\""
    }
}
