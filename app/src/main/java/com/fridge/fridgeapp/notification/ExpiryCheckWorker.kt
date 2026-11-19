package com.fridge.fridgeapp.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fridge.fridgeapp.database.AppDatabase

class ExpiryCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val foods = AppDatabase.getInstance(applicationContext)
                .foodDao()
                .getAllFoodOnce()
            ExpiryNotification.createChannel(applicationContext)
            ExpiryNotification.show(applicationContext, foods)
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
