package com.muka.pangolin

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast

class TToast {
    companion object {
        private var sToast: Toast? = null

        fun show(context: Context, msg: String) {
            show(context, msg, Toast.LENGTH_SHORT)
        }

        fun show(context: Context, msg: String, duration: Int) {
            val toast: Toast? = getToast(context)
            if (toast != null) {
                toast.duration = duration
                toast.setText(msg)
                toast.show()
            } else {
                Log.i("TToast", "toast msg: $msg")
            }
        }

        @SuppressLint("ShowToast")
        private fun getToast(context: Context?): Toast? {
            if (context == null) {
                return sToast
            }
            //        if (sToast == null) {
//            synchronized (TToast.class) {
//                if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT)
            //                }
//            }
//        }
            return sToast
        }

        fun reset() {
            sToast = null
        }
    }
}