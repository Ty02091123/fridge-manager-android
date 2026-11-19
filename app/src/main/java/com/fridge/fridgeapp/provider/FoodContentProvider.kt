package com.fridge.fridgeapp.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.fridge.fridgeapp.database.AppDatabase
import com.fridge.fridgeapp.database.Food
import com.fridge.fridgeapp.util.ExpiryUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FoodContentProvider : ContentProvider() {
    private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(FoodProviderContract.AUTHORITY, "food", FOOD)
        addURI(FoodProviderContract.AUTHORITY, "food/#", FOOD_ID)
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val context = checkNotNull(context)
        val foods = runBlocking(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(context).foodDao()
            when (matcher.match(uri)) {
                FOOD -> dao.getAllFoodOnce()
                FOOD_ID -> {
                    val id = ContentUris.parseId(uri).toInt()
                    listOfNotNull(dao.getFoodOnce(id))
                }
                else -> throw IllegalArgumentException("Unsupported URI: $uri")
            }
        }
        return foods.toCursor(projection)
    }

    override fun getType(uri: Uri): String {
        return when (matcher.match(uri)) {
            FOOD -> "vnd.android.cursor.dir/vnd.${FoodProviderContract.AUTHORITY}.food"
            FOOD_ID -> "vnd.android.cursor.item/vnd.${FoodProviderContract.AUTHORITY}.food"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        require(matcher.match(uri) == FOOD) { "Insert is only supported on the food collection" }
        val food = checkNotNull(values).toFood(id = 0)
        val id = runBlocking(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(checkNotNull(context)).foodDao()
            dao.insertFood(food).toInt()
        }
        val result = ContentUris.withAppendedId(FoodProviderContract.CONTENT_URI, id.toLong())
        notifyChange(result)
        return result
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        require(matcher.match(uri) == FOOD_ID) { "An item URI is required for update" }
        val id = ContentUris.parseId(uri).toInt()
        val context = checkNotNull(context)
        return runBlocking(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(context).foodDao()
            if (dao.getFoodOnce(id) == null) return@runBlocking 0
            dao.updateFood(checkNotNull(values).toFood(id))
            1
        }.also { if (it > 0) notifyChange(uri) }
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val context = checkNotNull(context)
        val deleted = runBlocking(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(context).foodDao()
            when {
                matcher.match(uri) == FOOD_ID -> {
                    val food = dao.getFoodOnce(ContentUris.parseId(uri).toInt())
                    if (food == null) 0 else {
                        dao.deleteFood(food)
                        1
                    }
                }
                matcher.match(uri) == FOOD &&
                    selection.equals(EXPIRED_SELECTION, ignoreCase = true) -> {
                    val today = selectionArgs?.firstOrNull() ?: ExpiryUtils.todayText()
                    dao.deleteExpiredBefore(today)
                }
                else -> throw UnsupportedOperationException(
                    "Use an item URI or the supported expired-food selection"
                )
            }
        }
        if (deleted > 0) notifyChange(uri)
        return deleted
    }

    private fun notifyChange(uri: Uri) {
        context?.contentResolver?.notifyChange(uri, null)
    }

    private fun List<Food>.toCursor(projection: Array<out String>?): Cursor {
        val columns = projection ?: FoodProviderContract.DEFAULT_PROJECTION
        val cursor = MatrixCursor(columns)
        forEach { food ->
            cursor.addRow(
                columns.map { column ->
                    when (column) {
                        FoodProviderContract.COLUMN_ID -> food.id
                        FoodProviderContract.COLUMN_NAME -> food.name
                        FoodProviderContract.COLUMN_TYPE -> food.type
                        FoodProviderContract.COLUMN_COUNT -> food.count
                        FoodProviderContract.COLUMN_EXPIRE_DATE -> food.expireDate
                        else -> null
                    }
                }.toTypedArray()
            )
        }
        return cursor
    }

    private fun ContentValues.toFood(id: Int) = Food(
        id = id,
        name = getAsString(FoodProviderContract.COLUMN_NAME).orEmpty(),
        type = getAsString(FoodProviderContract.COLUMN_TYPE).orEmpty(),
        count = getAsInteger(FoodProviderContract.COLUMN_COUNT) ?: 0,
        expireDate = getAsString(FoodProviderContract.COLUMN_EXPIRE_DATE).orEmpty()
    )

    companion object {
        private const val FOOD = 1
        private const val FOOD_ID = 2
        private const val EXPIRED_SELECTION = "expireDate < ?"
    }
}
