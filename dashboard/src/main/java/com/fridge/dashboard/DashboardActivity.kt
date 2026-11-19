package com.fridge.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fridge.dashboard.data.DashboardDateUtils
import com.fridge.dashboard.data.FoodDataSource
import com.fridge.dashboard.data.FoodItem
import com.fridge.dashboard.data.InventoryCsvExporter
import com.fridge.dashboard.databinding.ActivityDashboardBinding
import com.fridge.dashboard.ui.FoodSummaryAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val adapter = FoodSummaryAdapter()
    private var currentFoods: List<FoodItem> = emptyList()
    private val createCsvLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) writeCsv(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvFood.adapter = adapter
        binding.rvFood.layoutManager = LinearLayoutManager(this)
        binding.btnRefresh.setOnClickListener { loadData() }
        binding.btnRetry.setOnClickListener { loadData() }
        binding.btnOpenMain.setOnClickListener { openMainApp() }
        binding.btnExport.setOnClickListener { exportCsv() }
        binding.btnCleanup.setOnClickListener { confirmCleanupExpired() }
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
        binding.btnExport.isEnabled = false
        binding.btnCleanup.isEnabled = false
        binding.tvSummary.setText(R.string.dashboard_loading)
    }

    private fun renderContent(foods: List<FoodItem>) {
        currentFoods = foods
        binding.progressBar.visibility = View.GONE
        binding.errorState.visibility = View.GONE
        binding.rvFood.visibility = View.VISIBLE
        binding.emptyState.visibility = if (foods.isEmpty()) View.VISIBLE else View.GONE

        val expiring = foods.count { DashboardDateUtils.isExpiringSoon(it.expireDate) }
        val expired = foods.count { DashboardDateUtils.isExpired(it.expireDate) }
        val fresh = (foods.size - expiring - expired).coerceAtLeast(0)
        binding.expiryChart.setData(fresh, expiring, expired)
        binding.tvFreshCount.text = getString(R.string.chart_fresh, fresh)
        binding.tvExpiringCount.text = getString(R.string.chart_expiring, expiring)
        binding.tvExpiredCount.text = getString(R.string.chart_expired, expired)
        binding.btnExport.isEnabled = foods.isNotEmpty()
        binding.btnCleanup.isEnabled = expired > 0
        binding.tvSummary.text = getString(
            R.string.dashboard_summary,
            foods.size,
            foods.sumOf { it.count },
            expiring,
            expired
        )
    }

    private fun renderError(message: String) {
        currentFoods = emptyList()
        binding.progressBar.visibility = View.GONE
        binding.rvFood.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.VISIBLE
        binding.btnExport.isEnabled = false
        binding.btnCleanup.isEnabled = false
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

    private fun exportCsv() {
        if (currentFoods.isEmpty()) {
            Snackbar.make(binding.root, R.string.export_no_data, Snackbar.LENGTH_SHORT).show()
            return
        }
        createCsvLauncher.launch("fridge-inventory-${DashboardDateUtils.todayText()}.csv")
    }

    private fun writeCsv(uri: Uri) {
        lifecycleScope.launch {
            runCatching { InventoryCsvExporter.write(this@DashboardActivity, uri, currentFoods) }
                .onSuccess {
                    Snackbar.make(binding.root, R.string.export_success, Snackbar.LENGTH_LONG).show()
                }
                .onFailure {
                    Snackbar.make(binding.root, R.string.export_failed, Snackbar.LENGTH_LONG).show()
                }
        }
    }

    private fun confirmCleanupExpired() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.cleanup_title)
            .setMessage(R.string.cleanup_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.cleanup_confirm) { _, _ -> cleanupExpired() }
            .show()
    }

    private fun cleanupExpired() {
        lifecycleScope.launch {
            runCatching {
                FoodDataSource.deleteExpired(
                    this@DashboardActivity,
                    DashboardDateUtils.todayText()
                )
            }.onSuccess { count ->
                Snackbar.make(
                    binding.root,
                    getString(R.string.cleanup_success, count),
                    Snackbar.LENGTH_SHORT
                ).show()
                loadData()
            }.onFailure {
                Snackbar.make(binding.root, R.string.cleanup_failed, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
