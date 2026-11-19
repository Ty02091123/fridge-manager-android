package com.fridge.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridge.dashboard.data.DashboardDateUtils
import com.fridge.dashboard.data.FoodDataSource
import com.fridge.dashboard.data.FoodItem
import com.fridge.dashboard.databinding.ActivityDashboardBinding
import com.fridge.dashboard.ui.FoodSummaryAdapter
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val adapter = FoodSummaryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvFood.adapter = adapter
        binding.rvFood.layoutManager = LinearLayoutManager(this)
        binding.btnRefresh.setOnClickListener { loadData() }
        binding.btnRetry.setOnClickListener { loadData() }
        binding.btnOpenMain.setOnClickListener { openMainApp() }
        loadData()
    }

    private fun loadData() {
        renderLoading()
        lifecycleScope.launch {
            runCatching { FoodDataSource.load(this@DashboardActivity) }
                .onSuccess { foods ->
                    adapter.submitList(foods)
                    renderContent(foods)
                }
                .onFailure { renderError(it.localizedMessage.orEmpty()) }
        }
    }

    private fun renderLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorState.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.rvFood.visibility = View.GONE
        binding.tvSummary.setText(R.string.dashboard_loading)
    }

    private fun renderContent(foods: List<FoodItem>) {
        binding.progressBar.visibility = View.GONE
        binding.errorState.visibility = View.GONE
        binding.rvFood.visibility = View.VISIBLE
        binding.emptyState.visibility = if (foods.isEmpty()) View.VISIBLE else View.GONE

        val expiring = foods.count { DashboardDateUtils.isExpiringSoon(it.expireDate) }
        val expired = foods.count { DashboardDateUtils.isExpired(it.expireDate) }
        binding.tvSummary.text = getString(
            R.string.dashboard_summary,
            foods.size,
            foods.sumOf { it.count },
            expiring,
            expired
        )
    }

    private fun renderError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.rvFood.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.VISIBLE
        binding.tvError.text = message.ifBlank { getString(R.string.dashboard_error_hint) }
        binding.tvSummary.setText(R.string.dashboard_error_summary)
    }

    private fun openMainApp() {
        val intent = Intent().setClassName(
            "com.fridge.fridgeapp",
            "com.fridge.fridgeapp.MainActivity"
        )
        runCatching { startActivity(intent) }
            .onFailure { binding.tvError.setText(R.string.main_app_not_installed) }
    }
}
