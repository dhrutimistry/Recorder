package com.kotlin.mvvm.structure.ui.viewmodels

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.kotlin.mvvm.structure.R
import com.kotlin.mvvm.structure.base.BaseViewModel
import com.kotlin.mvvm.structure.database.Recording
import com.kotlin.mvvm.structure.ui.adapters.RecordingListAdapter
import io.reactivex.disposables.Disposable

class DashboardViewModel : BaseViewModel() {

    //Disposables
    private lateinit var subscription: Disposable

    var strError = MutableLiveData<String>("")
    var strTextViewMessage = MutableLiveData<String>(context.resources.getString(R.string.text_no_data))
    var booleanAlreadyLoginWithOtherDevice = MutableLiveData<Boolean>(false)
    var isDataAvailable = ObservableBoolean(true)
    var recordingListAdapter =RecordingListAdapter(this)

    //Adapter

    init {
        val arrayList:ArrayList<Recording> = appDatabase.recordingDao().getAll() as ArrayList<Recording>
        if (arrayList.isNotEmpty()) {
            recordingListAdapter.addData(arrayList)
        } else {
            isDataAvailable.set(false)
        }
    }

}