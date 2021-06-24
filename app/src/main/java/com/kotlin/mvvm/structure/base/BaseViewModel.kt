package com.kotlin.mvvm.structure.base

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.mvvm.structure.R
import com.kotlin.mvvm.structure.database.AppDatabase
import com.kotlin.mvvm.structure.network.PostApi
import com.kotlin.mvvm.structure.utils.AppConstants
import com.kotlin.mvvm.structure.utils.AppPreferences
import com.oneclick.utils.library.AppDateFormatUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

open class BaseViewModel : ViewModel() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var context: Application

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var appDateFormatUtils: AppDateFormatUtils

    @Inject
    lateinit var appDatabase: AppDatabase

    //Disposables
    private lateinit var subscription: Disposable


    var booleanLogoutSuccess = MutableLiveData<Boolean>(false)

    //Progress
    val isShowProgress = MutableLiveData<Boolean>(false)
    var strErrorBase = MutableLiveData<String>("")

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        MyApplication.appComponent.inject(this)
    }

    fun checkForceUpdate() {
        val mJsonObjectLogin = JSONObject()

        try {
            mJsonObjectLogin.put("device_type", AppConstants.DEVICE_TYPE)
            mJsonObjectLogin.put("app_version", MyApplication.mStringVersion)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val mRequestBody: RequestBody = mJsonObjectLogin.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())



    }

}