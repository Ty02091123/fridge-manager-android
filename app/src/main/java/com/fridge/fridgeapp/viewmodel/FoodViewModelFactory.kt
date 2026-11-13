package com.fridge.fridgeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fridge.fridgeapp.database.FoodDao

class FoodViewModelFactory(private val foodDao: FoodDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            return FoodViewModel(foodDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
