package com.fridge.dashboard.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object InventoryCsvExporter {
    fun buildCsv(foods: List<FoodItem>): String = buildString {
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

    suspend fun write(context: Context, uri: Uri, foods: List<FoodItem>) =
        withContext(Dispatchers.IO) {
            val output = context.contentResolver.openOutputStream(uri)
                ?: error("无法创建导出文件")
            output.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(buildCsv(foods))
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
