package com.kotlin.mvvm.structure.base

import android.app.Application
import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.kotlin.mvvm.structure.R
import com.kotlin.mvvm.structure.database.AppDatabase
import com.kotlin.mvvm.structure.databinding.BaseActivityBinding
import com.kotlin.mvvm.structure.ui.ActivityDashboard
import com.kotlin.mvvm.structure.ui.ActivitySplash
import com.kotlin.mvvm.structure.utils.*
import com.kotlin.mvvm.structure.utils.listeners.ConnectivityReceiver
import com.kotlin.mvvm.structure.utils.listeners.setSafeOnClickListener
import com.oneclick.utils.library.AppDateFormatUtils
import com.oneclick.utils.library.AppUtils
import javax.inject.Inject

open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    lateinit var applications: Application

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var appUtils: AppUtils

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var appDialogUtils: AppDialogUtils

    @Inject
    lateinit var appDateFormatUtils: AppDateFormatUtils

    @Inject
    lateinit var appDatabase: AppDatabase

    private lateinit var baseActivityBinding: BaseActivityBinding

    lateinit var appPermissions: AppPermissions

    lateinit var baseViewModel: BaseViewModel

    private lateinit var broadcastReceiver: BroadcastReceiver

    private var mSnackBar: Snackbar? = null

    private var dialogProgress: Dialog? = null
    private var dialogForceUpdate: Dialog? = null
    private var dialogExitApplication: Dialog? = null
    private var dialogLogoutFromApplication: Dialog? = null
    private var mDialogCrashLogout: Dialog? = null

    var isActivityAnimationDone = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyApplication.appComponent.inject(this)
        overrideAnimationStart()

        baseActivityBinding = DataBindingUtil.setContentView(this, R.layout.base_activity)
        baseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)

        appPermissions = AppPermissions(this)

        // Creates instance of the manager.
        val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)

        broadcastReceiver = ConnectivityReceiver()

        registerReceiver(
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        baseViewModel.strErrorBase.observe(this, Observer {
            if (it != "") {
                showMessage(it)
            }
        })



    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this

        if (MyApplication.isInternetAvailable) {
            if (this !is ActivitySplash) {
                //baseViewModel.checkForceUpdate()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (dialogProgress != null && dialogProgress!!.isShowing) {
            dialogProgress!!.dismiss()
        }

        if (dialogForceUpdate != null && dialogForceUpdate!!.isShowing) {
            dialogForceUpdate!!.dismiss()
        }

        if (dialogExitApplication != null && dialogExitApplication!!.isShowing) {
            dialogExitApplication!!.dismiss()
        }

        if (mDialogCrashLogout != null && mDialogCrashLogout!!.isShowing) {
            mDialogCrashLogout!!.dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overrideAnimationEnd()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.base_activity_toolbar)

        baseActivityBinding.baseActivityToolbarImageviewClose.setSafeOnClickListener {
            onBackPressed()
        }

    }

    protected open fun <T : ViewDataBinding?> putContentView(@LayoutRes resId: Int): T {
        val frameLayout = findViewById<ViewGroup?>(R.id.base_activity_fl_content)

        return DataBindingUtil.inflate<T>(
            layoutInflater,
            resId,
            frameLayout,
            true
        )
    }

    fun showMessage(strError: String) {
        mSnackBar = Snackbar.make(
            findViewById(R.id.base_activity_rl_container),
            strError,
            Snackbar.LENGTH_LONG
        ) //Assume "rootLayout" as the root layout of every activity.
        mSnackBar?.duration = BaseTransientBottomBar.LENGTH_LONG
        mSnackBar?.show()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        MyApplication.isInternetAvailable = isConnected
    }

    fun showExitDialog() {
        val mAlertDialogBuilder = AlertDialog.Builder(this)

        mAlertDialogBuilder.setTitle(resources.getString(R.string.text_exit_title))
            .setMessage(resources.getString(R.string.text_exit_information))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.text_yes)) { dialog, _ ->
                dialog.dismiss()
                this.finishAffinity()
            }
            .setNegativeButton(resources.getString(R.string.text_no)) { dialog, _ ->
                dialog.dismiss()
            }

        if (dialogExitApplication == null) {
            dialogExitApplication = mAlertDialogBuilder.create()
        }

        if (!dialogExitApplication!!.isShowing) {
            dialogExitApplication!!.show()
        }
    }

    fun showLogoutDialog() {
        val mAlertDialogBuilder = AlertDialog.Builder(this)

        mAlertDialogBuilder.setTitle(resources.getString(R.string.text_logout_title))
            .setMessage(resources.getString(R.string.text_logout_information))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.text_yes)) { dialog, _ ->
                dialog.dismiss()
                //baseViewModel.doUserLogout()
            }
            .setNegativeButton(resources.getString(R.string.text_no)) { dialog, _ ->
                dialog.dismiss()
            }

        if (dialogLogoutFromApplication == null) {
            dialogLogoutFromApplication = mAlertDialogBuilder.create()
        }

        if (!dialogLogoutFromApplication!!.isShowing) {
            dialogLogoutFromApplication!!.show()
        }
    }

    //Check If Animation Is Running When Sliding Activity
    open fun checkIsAnimationDone() {
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(resources.getInteger(android.R.integer.config_longAnimTime).toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    isActivityAnimationDone = true
                }
            }
        }
        thread.start()
    }

    //Override Animation On Going to Next Screen
    open fun overrideAnimationStart() {
        if (isActivityAnimationDone) {
            isActivityAnimationDone = false
            checkIsAnimationDone()

            if (this !is ActivityDashboard && this !is ActivitySplash) {
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
            }
        }
    }

    //Override Animation On Going to Previous Screen
    open fun overrideAnimationEnd() {
        if (isActivityAnimationDone) {
            isActivityAnimationDone = false
            checkIsAnimationDone()

            if (this !is ActivityDashboard) {
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
            }
        }
    }

    //Desc: This method sets up the toolbar according to the provided configuration.
    open fun setToolbarConfiguration(
        isToolbarVisible: Boolean,
        toolbarConfiguration: ToolbarConfiguration? = null
    ) {
        //First, initiating the toolbar
        initToolbar()
        //Now, Setting up the configuration
        if (isToolbarVisible) {
            baseActivityBinding.apply {
                baseActivityToolbar.isVisible = true
                toolbarConfiguration?.let { configuration ->
                    //Setting title
                    baseActivityToolbarTextviewTitle.text = configuration.title

                    //Setting up visibility configuration
                    baseActivityToolbarTextviewTitle.isVisible = configuration.isTitleVisible
                    baseActivityToolbarImageviewClose.isVisible = configuration.isBackButtonVisible
                }
            }

        } else {
            baseActivityBinding.baseActivityToolbar.isVisible = false
        }
    }


    //Desc: Date class used for configuring toolbar properties.
    data class ToolbarConfiguration(
        val title: String = "",
        val isTitleVisible: Boolean = true,//generally title is visible
        val isBackButtonVisible: Boolean = false,//generally back button is visible
    )



}