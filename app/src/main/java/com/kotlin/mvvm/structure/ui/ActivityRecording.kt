package com.kotlin.mvvm.structure.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kotlin.mvvm.structure.R
import com.kotlin.mvvm.structure.base.BaseActivity
import com.kotlin.mvvm.structure.databinding.ActivityRecordingBinding
import com.kotlin.mvvm.structure.ui.viewmodels.DashboardViewModel
import com.kotlin.mvvm.structure.utils.Recorder
import com.kotlin.mvvm.structure.utils.formatAsTime
import com.kotlin.mvvm.structure.utils.listeners.setSafeOnClickListener
import kotlin.math.sqrt

class ActivityRecording  : BaseActivity() {

    private lateinit var activityRecordingBinding: ActivityRecordingBinding
    lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var recorder: Recorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityRecordingBinding =
            putContentView(R.layout.activity_recording) as ActivityRecordingBinding

        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        activityRecordingBinding.dashboardViewModel = dashboardViewModel

        //Setting up the toolbar configuration
        setToolbarConfiguration(true,
            ToolbarConfiguration(
                title=getString(R.string.text_recording),
                isBackButtonVisible = true
            )
        )

        appPermissions.checkPermissions()

        dashboardViewModel.strError.observe(this, Observer {
            if (it != "") {
                showMessage(it)
            }
        })
        listenOnRecorderStates()
        Handler().postDelayed({
            recorder.toggleRecording()

        }, 1000)
        activityRecordingBinding.visualizer.ampNormalizer = { sqrt(it.toFloat()).toInt() }

        activityRecordingBinding.activityRecordingImageStop.setSafeOnClickListener {
            recorder.toggleRecording()

        }

    }

    private fun listenOnRecorderStates() = with(activityRecordingBinding) {
        recorder = Recorder.getInstance(applicationContext).init().apply {
            onStart = {  }
            onStop = {
                visualizer.clear()
                timelineTextView.text = 0L.formatAsTime()
                startActivity(Intent(this@ActivityRecording, ActivityPlayer::class.java))
                finish()

            }
            onAmpListener = {
                runOnUiThread {
                    if (recorder.isRecording) {
                        timelineTextView.text = recorder.getCurrentTime().formatAsTime()
                        visualizer.addAmp(it, tickDuration)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        listenOnRecorderStates()
    }

    override fun onStop() {
        recorder.release()
        super.onStop()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        recorder.onPause
        outState.putInt("position",recorder.tickDuration)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        recorder.onAmpListener = {
            runOnUiThread {
                if (recorder.isRecording) {
                    activityRecordingBinding.visualizer.addAmp(0,
                        savedInstanceState.getInt("position"))
                }
            }
        }
        super.onRestoreInstanceState(savedInstanceState)
    }


}