package com.kotlin.mvvm.structure.base

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.kotlin.mvvm.structure.database.AppDatabase
import com.kotlin.mvvm.structure.injection.component.AppComponent
import com.kotlin.mvvm.structure.injection.component.DaggerAppComponent
import com.kotlin.mvvm.structure.injection.modules.AppModule
import com.kotlin.mvvm.structure.injection.modules.NetworkModule

class MyApplication : Application() {

    companion object {
        lateinit var application: Application
        lateinit var appComponent: AppComponent
        var isInternetAvailable: Boolean = false
        var mStringVersion: String = ""
        var dialogDefaultButtonClick: MutableLiveData<Int> = MutableLiveData(0)
    }

    override fun onCreate() {
        super.onCreate()

        application = this

        val db = Room.databaseBuilder(
            this, AppDatabase::class.java, "Kotlin-MVVM-Structure"
        ).allowMainThreadQueries().build()

        appComponent = DaggerAppComponent.builder()
            .networkModule(NetworkModule)
            .appModule(AppModule(application, db))
            .build()

        appComponent.inject(this)

        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            mStringVersion = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

}
