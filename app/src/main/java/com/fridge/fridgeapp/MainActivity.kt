package com.fridge.fridgeapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridge.fridgeapp.database.Food
import com.fridge.fridgeapp.database.AppDatabase
import com.fridge.fridgeapp.databinding.ActivityMainBinding
import com.fridge.fridgeapp.notification.ExpiryReminderScheduler
import com.fridge.fridgeapp.ui.FoodAdapter
import com.fridge.fridgeapp.util.ExpiryUtils
import com.fridge.fridgeapp.viewmodel.FoodViewModel
import com.fridge.fridgeapp.viewmodel.FoodViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FoodViewModel
    private val adapter = FoodAdapter(
        onEdit = ::openFoodForm,
        onDelete = ::confirmDelete
    )
    private val formLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        saveFormResult(result.data ?: return@registerForActivityResult)
    }
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startExpiryCheck()
        } else {
            Snackbar.make(
                binding.root,
                R.string.notification_permission_denied,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

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
                    renderInventorySummary(foods)
                }
            }
        }

        binding.btnAdd.setOnClickListener { openFoodForm() }
        binding.btnCheckNow.setOnClickListener { requestExpiryCheck() }
        binding.btnRecipes.setOnClickListener {
            startActivity(Intent(this, RecipeActivity::class.java))
        }

        ExpiryReminderScheduler.schedule(this)
        requestNotificationPermissionIfNeeded()
    }

    private fun openFoodForm(food: Food? = null) {
        val intent = Intent(this, FoodFormActivity::class.java)
        food?.let {
            intent.putExtra(FoodFormActivity.EXTRA_ID, it.id)
            intent.putExtra(FoodFormActivity.EXTRA_NAME, it.name)
            intent.putExtra(FoodFormActivity.EXTRA_TYPE, it.type)
            intent.putExtra(FoodFormActivity.EXTRA_COUNT, it.count)
            intent.putExtra(FoodFormActivity.EXTRA_EXPIRE_DATE, it.expireDate)
        }
        formLauncher.launch(intent)
    }

    private fun saveFormResult(data: Intent) {
        val id = data.getIntExtra(FoodFormActivity.EXTRA_ID, 0)
        val food = Food(
            id = id,
            name = data.getStringExtra(FoodFormActivity.EXTRA_NAME).orEmpty(),
            type = data.getStringExtra(FoodFormActivity.EXTRA_TYPE).orEmpty(),
            count = data.getIntExtra(FoodFormActivity.EXTRA_COUNT, 1),
            expireDate = data.getStringExtra(FoodFormActivity.EXTRA_EXPIRE_DATE).orEmpty()
        )
        if (id == 0) {
            viewModel.insertFood(food)
        } else {
            viewModel.updateFood(food)
        }
        Snackbar.make(
            binding.root,
            getString(if (id == 0) R.string.food_added else R.string.food_updated),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun confirmDelete(food: Food) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_food_title)
            .setMessage(getString(R.string.delete_food_message, food.name))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteFood(food)
                Snackbar.make(binding.root, R.string.food_deleted, Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun renderInventorySummary(foods: List<Food>) {
        val expiring = foods.count { ExpiryUtils.isExpiringSoon(it.expireDate) }
        val expired = foods.count { ExpiryUtils.isExpired(it.expireDate) }
        binding.tvSummary.text = getString(
            R.string.inventory_summary,
            foods.size,
            expiring,
            expired
        )
        binding.emptyState.visibility =
            if (foods.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun requestExpiryCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startExpiryCheck()
        }
    }

    private fun startExpiryCheck() {
        ExpiryReminderScheduler.checkNow(this)
        Snackbar.make(binding.root, R.string.check_started, Snackbar.LENGTH_SHORT).show()
    }
}
