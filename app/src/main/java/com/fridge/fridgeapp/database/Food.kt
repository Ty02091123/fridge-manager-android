package com.fridge.fridgeapp.database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,         // 食材名称
    val expireDate: String,   // 过期日期
    val count: Int,           // 数量
    val type: String          // 分类：蔬菜/肉类/水果等
)
