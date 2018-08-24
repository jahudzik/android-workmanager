package com.example.background.workers

import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.Worker
import com.example.background.R

class BlurWorker : Worker() {

    override fun doWork(): Result {
        try {
            val picture = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.test)
            val blurredPicture = WorkerUtils.blurBitmap(picture, applicationContext)
            val uri = WorkerUtils.writeBitmapToFile(applicationContext, blurredPicture)
            WorkerUtils.makeStatusNotification("Output is $uri", applicationContext)
            return Result.SUCCESS
        } catch (throwable: Throwable) {
            Log.e("BlurWorker", "Error applying blur", throwable)
            return Result.FAILURE
        }
    }

}
