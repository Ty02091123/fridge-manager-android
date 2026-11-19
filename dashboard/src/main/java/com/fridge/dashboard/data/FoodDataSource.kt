package com.fridge.dashboard.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FoodDataSource {
    private const val AUTHORITY = "com.fridge.fridgeapp.provider"
    private val contentUri: Uri = Uri.parse("content://$AUTHORITY/food")

    suspend fun load(context: Context): List<FoodItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<FoodItem>()
        val cursor = context.contentResolver.query(
            contentUri,
            arrayOf("id", "name", "type", "count", "expireDate"),
            null,
            null,
            "expireDate ASC"
        ) ?: throw IllegalStateException("主 App 数据接口没有返回数据")

        cursor.use {
            val idIndex = it.getColumnIndexOrThrow("id")
            val nameIndex = it.getColumnIndexOrThrow("name")
            val typeIndex = it.getColumnIndexOrThrow("type")
            val countIndex = it.getColumnIndexOrThrow("count")
            val expireIndex = it.getColumnIndexOrThrow("expireDate")
            while (it.moveToNext()) {
                items += FoodItem(
                    id = it.getInt(idIndex),
                    name = it.getString(nameIndex),
                    type = it.getString(typeIndex),
                    count = it.getInt(countIndex),
                    expireDate = it.getString(expireIndex)
                )
            }
        }
        items
    }

    suspend fun deleteExpired(context: Context, today: String): Int =
        withContext(Dispatchers.IO) {
            context.contentResolver.delete(
                contentUri,
                "expireDate < ?",
                arrayOf(today)
            )
        }
}
