package com.example.background.workers

import android.util.Log
import androidx.work.Worker
import com.example.background.Constants
import java.io.File

class CleanupWorker : Worker() {

    override fun doWork(): Result {
        try {
            val outputDirectory = File(applicationContext.filesDir, Constants.OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null && entries.isNotEmpty()) {
                    for (entry in entries) {
                        val name = entry.name
                        if (name.isNotEmpty() && name.endsWith(".png")) {
                            val deleted = entry.delete()
                            Log.d("CleanupWorker", "Deleted $name - $deleted")
                        }
                    }
                }
            }
            return Result.SUCCESS
        } catch (e: Exception) {
            Log.e("CleanupWorker", "Error cleaning up", e)
            return Result.FAILURE
        }
    }

}
