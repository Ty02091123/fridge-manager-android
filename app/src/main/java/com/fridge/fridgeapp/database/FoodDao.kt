package com.fridge.fridgeapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    // 查询全部食材
    @Query("SELECT * FROM food ORDER BY expireDate ASC")
    fun getAllFood(): Flow<List<Food>>

    // 根据id查询
    @Query("SELECT * FROM food WHERE id = :foodId")
    fun getFoodById(foodId: Int): Flow<Food>

    // 新增
    @Insert
    suspend fun insertFood(food: Food)

    // 修改
    @Update
    suspend fun updateFood(food: Food)

    // 删除
    @Delete
    suspend fun deleteFood(food: Food)
}
