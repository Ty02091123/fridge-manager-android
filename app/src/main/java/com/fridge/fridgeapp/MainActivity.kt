package com.fridge.fridgeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridge.fridgeapp.database.AppDatabase
import com.fridge.fridgeapp.databinding.ActivityMainBinding
import com.fridge.fridgeapp.ui.FoodAdapter
import com.fridge.fridgeapp.viewmodel.FoodViewModel
import com.fridge.fridgeapp.viewmodel.FoodViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FoodViewModel
    private val adapter = FoodAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化数据库
        val db = AppDatabase.getInstance(this)
        val dao = db.foodDao()
        viewModel = ViewModelProvider(this, FoodViewModelFactory(dao))[FoodViewModel::class.java]

        binding.rvFood.adapter = adapter
        binding.rvFood.layoutManager = LinearLayoutManager(this)

        // Flow 在界面可见时持续收集，避免 Activity 销毁后继续更新列表。
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFood.collect { foods ->
                    adapter.submitList(foods)
                }
            }
        }

        // 添加按钮，先写一个测试数据
        binding.btnAdd.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.insertFood(
                    com.fridge.fridgeapp.database.Food(
                        name = "西红柿",
                        expireDate = "2026-12-01",
                        count = 3,
                        type = "蔬菜"
                    )
                )
            }
        }
    }
}
