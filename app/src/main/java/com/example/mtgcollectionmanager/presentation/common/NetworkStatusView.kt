package com.example.mtgcollectionmanager.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager


class NetworkStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var messageTextView: TextView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_network_status, this, true)
        messageTextView = view.findViewById(R.id.tvNetworkStatus)
        visibility = GONE // Initially hidden
    }


    fun updateNetworkStatus(state: NetworkConnectivityManager.NetworkState) {
        when (state) {
            is NetworkConnectivityManager.NetworkState.Available -> {
                visibility = GONE
            }

            is NetworkConnectivityManager.NetworkState.Unavailable -> {
                messageTextView.text = context.getString(R.string.error_network)
                visibility = VISIBLE
            }
        }
    }
}