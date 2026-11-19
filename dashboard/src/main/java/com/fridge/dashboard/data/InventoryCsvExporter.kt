package com.fridge.dashboard.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object InventoryCsvExporter {
    suspend fun write(context: Context, uri: Uri, foods: List<FoodItem>) =
        withContext(Dispatchers.IO) {
            val output = context.contentResolver.openOutputStream(uri)
                ?: error("无法创建导出文件")
            output.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(InventoryCsvFormatter.build(foods))
            }
        }
}
