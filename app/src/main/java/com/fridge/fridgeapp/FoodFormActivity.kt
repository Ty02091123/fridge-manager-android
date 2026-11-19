package com.fridge.fridgeapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fridge.fridgeapp.databinding.ActivityFoodFormBinding
import com.fridge.fridgeapp.util.ExpiryUtils
import java.util.Calendar
import java.util.Locale

class FoodFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodFormBinding
    private var editingId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingId = intent.getIntExtra(EXTRA_ID, 0)
        setupToolbar()
        setupTypeDropDown()
        setupDatePicker()
        restoreOrPopulateForm(savedInstanceState)

        binding.btnSave.setOnClickListener { submitForm() }
    }

    private fun setupToolbar() {
        binding.toolbar.title =
            getString(if (editingId == 0) R.string.add_food else R.string.edit_food)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupTypeDropDown() {
        val types = resources.getStringArray(R.array.food_types)
        binding.actType.setSimpleItems(types)
        binding.actType.setOnClickListener { binding.actType.showDropDown() }
    }

    private fun setupDatePicker() {
        binding.etExpire.setOnClickListener { showDatePicker() }
        binding.tilExpire.setEndIconOnClickListener { showDatePicker() }
    }

    private fun restoreOrPopulateForm(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) return

        if (editingId == 0) {
            binding.etExpire.setText(ExpiryUtils.todayText())
            binding.actType.setText(getString(R.string.food_type_vegetable), false)
            binding.etCount.setText("1")
            return
        }

        binding.etName.setText(intent.getStringExtra(EXTRA_NAME))
        binding.actType.setText(intent.getStringExtra(EXTRA_TYPE), false)
        binding.etCount.setText(intent.getIntExtra(EXTRA_COUNT, 1).toString())
        binding.etExpire.setText(intent.getStringExtra(EXTRA_EXPIRE_DATE))
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val current = binding.etExpire.text?.toString()
        if (!current.isNullOrBlank()) {
            runCatching {
                val parts = current.split("-")
                calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            }
        }

        DatePickerDialog(
            this,
            { _, year, month, day ->
                binding.etExpire.setText(
                    String.format(Locale.CHINA, "%04d-%02d-%02d", year, month + 1, day)
                )
                binding.tilExpire.error = null
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun submitForm() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val type = binding.actType.text?.toString()?.trim().orEmpty()
        val count = binding.etCount.text?.toString()?.toIntOrNull()
        val expireDate = binding.etExpire.text?.toString()?.trim().orEmpty()

        binding.tilName.error = if (name.isBlank()) getString(R.string.error_food_name) else null
        binding.tilType.error = if (type.isBlank()) getString(R.string.error_food_type) else null
        binding.tilCount.error =
            if (count == null || count <= 0) getString(R.string.error_food_count) else null
        binding.tilExpire.error =
            if (expireDate.isBlank()) getString(R.string.error_expire_date) else null

        if (name.isBlank() || type.isBlank() || count == null || count <= 0 || expireDate.isBlank()) {
            return
        }

        val result = Intent().apply {
            putExtra(EXTRA_ID, editingId)
            putExtra(EXTRA_NAME, name)
            putExtra(EXTRA_TYPE, type)
            putExtra(EXTRA_COUNT, count)
            putExtra(EXTRA_EXPIRE_DATE, expireDate)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    companion object {
        const val EXTRA_ID = "food_id"
        const val EXTRA_NAME = "food_name"
        const val EXTRA_TYPE = "food_type"
        const val EXTRA_COUNT = "food_count"
        const val EXTRA_EXPIRE_DATE = "food_expire_date"
    }
}
