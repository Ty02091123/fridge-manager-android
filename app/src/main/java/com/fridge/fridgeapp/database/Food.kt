package com.fridge.fridgeapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,       // 食材名字
    val count: Int,         // 数量
    val expireDate: String, // 过期日期
    val remark: String      // 备注
)
