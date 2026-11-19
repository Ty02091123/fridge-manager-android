package com.fridge.fridgeapp.provider

import android.net.Uri

object FoodProviderContract {
    const val AUTHORITY = "com.fridge.fridgeapp.provider"
    const val READ_PERMISSION = "com.fridge.fridgeapp.permission.READ_FOOD_DATA"
    const val WRITE_PERMISSION = "com.fridge.fridgeapp.permission.WRITE_FOOD_DATA"

    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_TYPE = "type"
    const val COLUMN_COUNT = "count"
    const val COLUMN_EXPIRE_DATE = "expireDate"

    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/food")

    val DEFAULT_PROJECTION = arrayOf(
        COLUMN_ID,
        COLUMN_NAME,
        COLUMN_TYPE,
        COLUMN_COUNT,
        COLUMN_EXPIRE_DATE
    )
}
