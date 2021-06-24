package com.kotlin.mvvm.structure.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.kotlin.mvvm.structure.R

class AppDialogUtils(var context: Context) {

    companion object {
        fun newInstance(context: Context): AppDialogUtils {
            return AppDialogUtils(context)
        }


    }

    //Desc: For Generating Default Dialog according to the provided parameters.
    open fun getNewDialog(
        messageText: String,
        positiveText: String,
        positiveButtonClick: (dialog: DialogInterface) -> Unit,
        negativeText: String? = null,
        negativeButtonClick: ((dialog: DialogInterface) -> Unit)? = null,
        titleText: String = context.getString(R.string.app_name),
        isCancelable: Boolean = true
    ): Dialog {
        val builder =
            AlertDialog.Builder(context, R.style.AlertDialogStyleAppCompat)
        builder.setTitle(titleText)
        builder.setCancelable(isCancelable)
        builder.setMessage(messageText)
        builder.setPositiveButton(
            positiveText
        ) { dialog, _ ->
            positiveButtonClick(dialog)
        }
        if (negativeText != null) {
            builder.setNegativeButton(negativeText) { dialog, _ ->
                if (negativeButtonClick != null) {
                    negativeButtonClick(dialog)
                }
            }
        }

        return builder.create()

    }

}