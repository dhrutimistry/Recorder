package com.kotlin.mvvm.structure.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.kotlin.mvvm.structure.R
import com.kotlin.mvvm.structure.base.BaseActivity
import com.kotlin.mvvm.structure.database.Recording
import com.kotlin.mvvm.structure.databinding.ActivityPlayerBinding
import com.kotlin.mvvm.structure.ui.viewmodels.DashboardViewModel
import com.kotlin.mvvm.structure.utils.AudioPlayer
import com.kotlin.mvvm.structure.utils.listeners.setSafeOnClickListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class ActivityPlayer : BaseActivity() {

    private lateinit var activityPlayerBinding: ActivityPlayerBinding
    lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var player: AudioPlayer
    var position:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityPlayerBinding =
            putContentView(R.layout.activity_player) as ActivityPlayerBinding

        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        activityPlayerBinding.dashboardViewModel = dashboardViewModel

        //Setting up the toolbar configuration
        setToolbarConfiguration(true,
            ToolbarConfiguration(
                title=getString(R.string.text_preview),
                isBackButtonVisible = true
            )
        )

        appPermissions.checkPermissions()


        activityPlayerBinding.activityRecordingImageUndo.setSafeOnClickListener {
            startActivity(Intent(this,ActivityRecording::class.java))
            finish()
        }

        activityPlayerBinding.activityRecordingSave.setSafeOnClickListener {
            activityPlayerBinding.activityPlayerBottomSaveView.visibility = View.VISIBLE
        }

        activityPlayerBinding.activityPlayerButtonSave.setSafeOnClickListener {
            if (activityPlayerBinding.activityPlayerEdittextName.text!!.isNotEmpty()){
                var recordingData = Recording(
                    0, activityPlayerBinding.activityPlayerEdittextName.text.toString(),
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    ,player.recordFile.toString()
                )
                appDatabase.recordingDao().insert(recordingData)

                startActivity(Intent(this,ActivityDashboard::class.java))
                finish()
            }else {
                Toast.makeText(this,"Please enter file name",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        listenOnPlayerStates()
        initUI()
    }

    override fun onStop() {
        player.release()
        super.onStop()
    }

    private fun initUI() = with(activityPlayerBinding) {
        visualizer.apply {
            ampNormalizer = { sqrt(it.toFloat()).toInt() }
            onStartSeeking = {
                player.pause()
            }
            onSeeking = {  }
            onFinishedSeeking = { time, isPlayingBefore ->
                player.seekTo(time)
                if (isPlayingBefore) {
                    player.resume()
                }
            }
            onAnimateToPositionFinished = { time, isPlaying ->
                updateTime(time, isPlaying)
                player.seekTo(time)
            }
        }
        activityRecordingImagePlay.setSafeOnClickListener { player.togglePlay() }


        lifecycleScope.launchWhenCreated {
            val amps = player.loadAmps()
            visualizer.setWaveForm(amps, player.tickDuration)
        }
    }

    private fun listenOnPlayerStates() = with(activityPlayerBinding) {
        player = AudioPlayer.getInstance(applicationContext).init().apply {
            onStart = { activityRecordingImagePlay.visibility = View.GONE }
            onStop = { activityRecordingImagePlay.visibility = View.VISIBLE }
            onPause = {  }
            onResume = {  }
            onProgress = { time, isPlaying -> updateTime(time, isPlaying) }
        }
    }

    private fun updateTime(time: Long, isPlaying: Boolean) = with(activityPlayerBinding) {
        visualizer.updateTime(time, isPlaying)
    }

    companion object {
        const val SEEK_OVER_AMOUNT = 5000
    }


    override fun onSaveInstanceState(outState: Bundle) {

        outState.putLong("position", player.player.currentPosition)

        if (activityPlayerBinding.activityPlayerBottomSaveView.visibility == View.VISIBLE){
            outState.putString("file_name",activityPlayerBinding.activityPlayerEdittextName.toString())
        }
        super.onSaveInstanceState(outState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        position = savedInstanceState.getLong("position")
        player.seekTo(position)

        player.togglePlay()
        if (savedInstanceState.getString("file_name")!!.isNotEmpty()){
            activityPlayerBinding.activityPlayerBottomSaveView.visibility = View.VISIBLE
            activityPlayerBinding.activityPlayerEdittextName.setText(savedInstanceState.getString("file_name"))
        }
        super.onRestoreInstanceState(savedInstanceState)

    }
}