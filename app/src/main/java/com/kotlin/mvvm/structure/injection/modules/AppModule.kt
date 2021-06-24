package com.kotlin.mvvm.structure.injection.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.kotlin.mvvm.structure.R
import com.kotlin.mvvm.structure.database.AppDatabase
import com.kotlin.mvvm.structure.utils.*
import com.oneclick.utils.library.AppDateFormatUtils
import com.oneclick.utils.library.AppUtils
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

@Module
class AppModule(private val application: Application, private val appDatabase: AppDatabase) {

    @Provides
    @Reusable
    internal fun providerApplication(): Application {
        return application
    }

    @Provides
    @Reusable
    internal fun providerContext(): Context {
        return application
    }

    @Provides
    @Reusable
    internal fun providesSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(
            application.resources.getString(R.string.app_name),
            0
        )
    }

    @Provides
    @Reusable
    internal fun providesSharedPreferencesEditor(): SharedPreferences.Editor {
        return providesSharedPreferences().edit()
    }

    @Provides
    @Reusable
    internal fun providerUtils(): AppUtils {
        return AppUtils.newInstance(application)
    }

    @Provides
    @Reusable
    internal fun providerPreferences(): AppPreferences {
        return AppPreferences.newInstance(application)
    }

    @Provides
    @Reusable
    internal fun providerDialogUtils(): AppDialogUtils {
        return AppDialogUtils.newInstance(providerContext())
    }

    @Provides
    @Reusable
    internal fun providerDateFormatUtils(): AppDateFormatUtils {
        return AppDateFormatUtils.newInstance(application)
    }

    @Provides
    @Singleton
    internal fun providerAPPDatabase(): AppDatabase {
        return appDatabase
    }

}