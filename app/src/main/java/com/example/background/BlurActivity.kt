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


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioGroup
import androidx.work.WorkStatus
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_blur.*


class BlurActivity : AppCompatActivity() {

    private var mViewModel: BlurViewModel? = null

    private val blurLevel: Int
        get() {
            val radioGroup = findViewById<RadioGroup>(R.id.radio_blur_group)
            return when (radioGroup.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> 1
                R.id.radio_blur_lv_2 -> 2
                R.id.radio_blur_lv_3 -> 3
                else -> 1
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur)
        initViewModel()
        showImage()
        setupButtons()
    }

    private fun initViewModel() {
        // Get the ViewModel
        mViewModel = ViewModelProviders.of(this).get(BlurViewModel::class.java)

        // Image uri should be stored in the ViewModel; put it there then display
        val intent = intent
        val imageUriExtra = intent.getStringExtra(Constants.KEY_IMAGE_URI)
        mViewModel?.setImageUri(imageUriExtra)

        mViewModel?.workStatus?.observe(this, Observer<List<WorkStatus>> { workStatuses ->
            if (workStatuses == null || workStatuses.isEmpty()) {
                return@Observer
            }
            if (workStatuses[0].state.isFinished) {
                showWorkFinished()
            } else {
                showWorkInProgress()
            }
        })
    }

    private fun showImage() {
        if (mViewModel?.imageUri != null) {
            Glide.with(this).load(mViewModel?.imageUri).into(image_view)
        }
    }

    private fun setupButtons() {
        // Setup blur image file button
        goButton.setOnClickListener { mViewModel?.applyBlur(blurLevel) }
    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        progressBar.visibility = View.VISIBLE
        cancelButton.visibility = View.VISIBLE
        goButton.visibility = View.GONE
        outputButton.visibility = View.GONE
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        progressBar.visibility = View.GONE
        cancelButton.visibility = View.GONE
        goButton.visibility = View.VISIBLE
    }
}
