package com.example.background.workers

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import com.example.background.Constants

class BlurWorker : Worker() {

    override fun doWork(): Result {
        val resourceUri = inputData.getString(Constants.KEY_IMAGE_URI)
        try {
            if (resourceUri == null || resourceUri.isEmpty()) {
                Log.e("BlurWorker", "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }
            val picture = BitmapFactory.decodeStream(applicationContext.contentResolver.openInputStream(Uri.parse(resourceUri)))
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
