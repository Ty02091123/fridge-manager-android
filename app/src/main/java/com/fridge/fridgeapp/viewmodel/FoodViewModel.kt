package com.fridge.fridgeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fridge.fridgeapp.database.Food
import com.fridge.fridgeapp.database.FoodDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodViewModel(private val foodDao: FoodDao) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<InventoryUiState> = refreshTrigger
        .flatMapLatest {
            foodDao.getAllFood()
                .map<List<Food>, InventoryUiState> { InventoryUiState.Content(it) }
                .catch { error ->
                    emit(
                        InventoryUiState.Error(
                            error.localizedMessage ?: "未知数据库错误"
                        )
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InventoryUiState.Loading
        )

    fun insertFood(food: Food) = viewModelScope.launch {
        foodDao.insertFood(food)
    }

    fun updateFood(food: Food) = viewModelScope.launch {
        foodDao.updateFood(food)
    }

    fun deleteFood(food: Food) = viewModelScope.launch {
        foodDao.deleteFood(food)
    }

    fun retry() {
        refreshTrigger.value += 1
    }
}

sealed interface InventoryUiState {
    data object Loading : InventoryUiState
    data class Content(val foods: List<Food>) : InventoryUiState
    data class Error(val message: String) : InventoryUiState
}
