package com.kotlin.mvvm.structure.utils

import android.app.Application
import android.content.SharedPreferences
import com.kotlin.mvvm.structure.base.MyApplication
import javax.inject.Inject

class AppPreferences(var application: Application) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    companion object {
        fun newInstance(application: Application): AppPreferences {
            return AppPreferences(application)
        }
    }

    init {
        MyApplication.appComponent.inject(this)
    }



}