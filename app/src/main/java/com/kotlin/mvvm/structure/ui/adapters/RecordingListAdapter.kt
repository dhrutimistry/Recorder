package com.kotlin.mvvm.structure.ui.adapters

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.mvvm.structure.database.Recording
import com.kotlin.mvvm.structure.databinding.RawRecordedItemBinding
import com.kotlin.mvvm.structure.ui.viewmodels.DashboardViewModel
import com.kotlin.mvvm.structure.utils.listeners.setSafeOnClickListener
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RecordingListAdapter(private val dashboardViewModel: DashboardViewModel) :
    RecyclerView.Adapter<RecordingListAdapter.AcademicListViewHolder>() {

    var arrayListRecording: ArrayList<Recording> = ArrayList()
    private var mSelectedItem = -1
    var mediaPlayer =  MediaPlayer()

    inner class AcademicListViewHolder(val rawRecordedItemBinding: RawRecordedItemBinding) :
        RecyclerView.ViewHolder(rawRecordedItemBinding.root) {
        fun bind(mData: Recording) {
            rawRecordedItemBinding.dashboardViewModel = dashboardViewModel
            rawRecordedItemBinding.recording = mData
        }
    }

    fun addData(arrayListRecording: ArrayList<Recording>) {
        this.arrayListRecording.addAll(arrayListRecording)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcademicListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RawRecordedItemBinding.inflate(inflater)
        return AcademicListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayListRecording.size
    }

    override fun onBindViewHolder(holder: AcademicListViewHolder, position: Int) {
        holder.bind(arrayListRecording[position])
        val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val recordingtime  = SimpleDateFormat("HH:mm").parse(arrayListRecording[position].time)
        var currentTimeDate = SimpleDateFormat("HH:mm").parse(currentTime)

        val difference: Long = currentTimeDate.time - recordingtime.time
        var days = (difference / (1000 * 60 * 60 * 24)).toInt()
        var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        var min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)
        hours = if (hours < 0) -hours else hours

        when {
            days != 0 -> {
                holder.rawRecordedItemBinding.rawRecordedItemTime.text = "$days days ago  •  ${arrayListRecording[position].time}"
            }
            hours.toInt() != 0 -> {
                holder.rawRecordedItemBinding.rawRecordedItemTime.text = "$hours hr ago  •  ${arrayListRecording[position].time}"
            }
            min.toInt() != 0 -> {
                holder.rawRecordedItemBinding.rawRecordedItemTime.text = "$min min ago  •  ${arrayListRecording[position].time}"
            }
        }
        Log.i("======= Hours", " :: $hours $min $days")
        if (mSelectedItem == position) {
            holder.rawRecordedItemBinding.animationView.visibility = View.VISIBLE
        }else{
            holder.rawRecordedItemBinding.animationView.visibility = View.GONE
        }

        holder.rawRecordedItemBinding.container.setSafeOnClickListener {

            if (mSelectedItem >= 0)
                notifyItemChanged(mSelectedItem);
            mSelectedItem = holder.bindingAdapterPosition
            notifyItemChanged(mSelectedItem)

            mediaPlayer = MediaPlayer()
            var myUri = arrayListRecording[position].file
            if (myUri!!.contains("/document/raw:/")) {
                myUri = myUri.removeRange(0,14)
            }
            Log.d("-----------",myUri)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }


            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(myUri)
            mediaPlayer.prepare()
            mediaPlayer.start()


        }

        mediaPlayer.setOnCompletionListener {
            mediaPlayer.stop()
            holder.rawRecordedItemBinding.animationView.visibility = View.GONE

        }

    }

}