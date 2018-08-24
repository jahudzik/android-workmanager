package com.example.background.workers

import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import com.example.background.Constants
import java.text.SimpleDateFormat
import java.util.*

class SaveImageToFileWorker : Worker() {

    private val TITLE = "Blurred Image"
    private val DATE_FORMATTER = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())

    override fun doWork(): Result {
        try {
            val contentResolver = applicationContext.contentResolver
            val resourceUri = inputData.getString(Constants.KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(resourceUri)))
            val imageUri = MediaStore.Images.Media.insertImage(contentResolver, bitmap, TITLE, DATE_FORMATTER.format(Date()))
            if (imageUri.isEmpty()) {
                Log.e("SaveImageToFileWorker", "Writing to MediaStore failed")
                return Result.FAILURE
            }
            outputData = Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, imageUri)
                    .build()
            return Result.SUCCESS
        } catch (e: Exception) {
            Log.e("SaveImageToFileWorker", "Unable to save image to Gallery", e)
            return Result.FAILURE
        }
    }

}
