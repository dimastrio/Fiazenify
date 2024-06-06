package id.dimas.fiazenify.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

open class ConnectionReceiver : BroadcastReceiver() {

    private var listener: ReceiverListener? = null


    override fun onReceive(context: Context?, intent: Intent?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = cm.activeNetworkInfo

        val callback = object : ConnectivityManager.NetworkCallback() {

        }

        if (listener != null) {

            val isConnected: Boolean = networkInfo != null && networkInfo.isConnectedOrConnecting

            listener!!.onNetworkChange(isConnected)

        }
    }

    interface ReceiverListener {
        fun onNetworkChange(isConnected: Boolean)
    }
}