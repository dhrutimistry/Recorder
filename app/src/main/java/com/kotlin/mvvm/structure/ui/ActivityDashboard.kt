package com.kotlin.mvvm.structure.ui

import android.R.attr
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.CursorLoader
import com.kotlin.mvvm.structure.R
import com.kotlin.mvvm.structure.base.BaseActivity
import com.kotlin.mvvm.structure.database.Recording
import com.kotlin.mvvm.structure.databinding.ActivityDashboardBinding
import com.kotlin.mvvm.structure.ui.adapters.RecordingListAdapter
import com.kotlin.mvvm.structure.ui.viewmodels.DashboardViewModel
import com.kotlin.mvvm.structure.utils.listeners.setSafeOnClickListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ActivityDashboard : BaseActivity() {

    private lateinit var activityDashboardBinding: ActivityDashboardBinding
    lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityDashboardBinding =
            putContentView(R.layout.activity_dashboard) as ActivityDashboardBinding

        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        activityDashboardBinding.dashboardViewModel = dashboardViewModel

        //Setting up the toolbar configuration
        setToolbarConfiguration(true,
            ToolbarConfiguration(
                title=getString(R.string.text_create_podcast),
                isBackButtonVisible = true
            )
        )

        appPermissions.checkPermissions()

        dashboardViewModel.strError.observe(this, Observer {
            if (it != "") {
                showMessage(it)
            }
        })

        activityDashboardBinding.activityDashboardImageRecord.setSafeOnClickListener {
            var intent = Intent(this,ActivityRecording::class.java)
            startActivity(intent)
        }

        activityDashboardBinding.activityDashboardImageFile.setSafeOnClickListener {
            val intent = Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 1)

        }

    }

    override fun onBackPressed() {
        showExitDialog()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode === 1) {
            if (resultCode === RESULT_OK) {

                //the selected audio.
                val uri: Uri = data?.data!!
                val file = File(uri.path)


                var recordingData = Recording(
                    0, file.name,
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    ,file.toString()
                )
                appDatabase.recordingDao().insert(recordingData)
                var adapter = RecordingListAdapter(dashboardViewModel)
                val arrayList:ArrayList<Recording> = appDatabase.recordingDao().getAll() as ArrayList<Recording>
                adapter.addData(arrayList)
                activityDashboardBinding.activityDashboardRecyclerviewRecording.adapter = adapter
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}