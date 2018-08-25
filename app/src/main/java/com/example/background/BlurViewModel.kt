/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background

import android.arch.lifecycle.ViewModel
import android.net.Uri
import android.text.TextUtils
import androidx.work.*
import com.example.background.workers.BlurWorker
import com.example.background.workers.CleanupWorker
import com.example.background.workers.SaveImageToFileWorker

class BlurViewModel : ViewModel() {

    internal var imageUri: Uri? = null
        private set
    private val workManager = WorkManager.getInstance()
    val workStatus = workManager.getStatusesByTag(Constants.TAG_OUTPUT)

    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     *
     * @param blurLevel The amount to blur the image
     */
    internal fun applyBlur(blurLevel: Int) {
        val chargingConstraint = Constraints.Builder()
                .setRequiresCharging(true)
                .build()

        val cleanupRequest = OneTimeWorkRequest.from(CleanupWorker::class.java)
        val blurRequest = OneTimeWorkRequest.Builder(BlurWorker::class.java)
                .setInputData(createInputDataForUri())
                .build()
        val saveImageRequest = OneTimeWorkRequest.Builder(SaveImageToFileWorker::class.java)
                .setConstraints(chargingConstraint)
                .addTag(Constants.TAG_OUTPUT)
                .build()
        workManager
                .beginUniqueWork(Constants.IMAGE_MANIPULATION_WORK_NAME, ExistingWorkPolicy.REPLACE, cleanupRequest)
                .then(blurRequest)
                .then(saveImageRequest)
                .enqueue()
    }

    fun cancelWork() {
        workManager.cancelUniqueWork(Constants.IMAGE_MANIPULATION_WORK_NAME)
    }

    private fun uriOrNull(uriString: String): Uri? {
        return if (!TextUtils.isEmpty(uriString)) {
            Uri.parse(uriString)
        } else null
    }

    internal fun setImageUri(uri: String) {
        imageUri = uriOrNull(uri)
    }

    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        if (imageUri != null) {
            builder.putString(Constants.KEY_IMAGE_URI, imageUri.toString())
        }
        return builder.build()
    }

}
