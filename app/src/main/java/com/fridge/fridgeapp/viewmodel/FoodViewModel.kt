package com.fridge.fridgeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fridge.fridgeapp.database.Food
import com.fridge.fridgeapp.database.FoodDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FoodViewModel(private val foodDao: FoodDao) : ViewModel() {

    val allFood: Flow<List<Food>> = foodDao.getAllFood()

    fun insertFood(food: Food) = viewModelScope.launch {
        foodDao.insertFood(food)
    }

    fun updateFood(food: Food) = viewModelScope.launch {
        foodDao.updateFood(food)
    }

    fun deleteFood(food: Food) = viewModelScope.launch {
        foodDao.deleteFood(food)
    }
}
