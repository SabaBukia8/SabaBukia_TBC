package com.example.sababukia_tbc.presentation.screen.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class BroadcastFragment : Fragment() {

    private lateinit var statusText: TextView
    private lateinit var sendButton: Button

    companion object {
        const val CUSTOM_ACTION = "com.example.sababukia_tbc.MY_CUSTOM_BROADCAST"
    }

    // Receiver 1: Airplane Mode
    private val airplaneModeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Keep onReceive fast & simple
            val isOn = intent.getBooleanExtra("state", false)
            statusText.text = if (isOn) "Airplane Mode: ON" else "Airplane Mode: OFF"
        }
    }

    // Receiver 2: Custom Broadcast
    private val customReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("message") ?: "No message"
            // Short UI feedback
            Toast.makeText(requireContext(), "Received: $message", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            com.example.sababukia_tbc.R.layout.fragment_broadcast,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusText = view.findViewById(com.example.sababukia_tbc.R.id.statusText)
        sendButton = view.findViewById(com.example.sababukia_tbc.R.id.sendButton)

        sendButton.setOnClickListener {
            sendCustomBroadcast()
        }
    }

    override fun onStart() {
        super.onStart()

        // Register airplane mode receiver (dynamic)
        val airplaneFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requireContext().registerReceiver(
                airplaneModeReceiver,
                airplaneFilter,
                Context.RECEIVER_EXPORTED
            )
        } else {
            requireContext().registerReceiver(airplaneModeReceiver, airplaneFilter)
        }

        // Register custom receiver (app-internal) as not exported
        val customFilter = IntentFilter(CUSTOM_ACTION)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requireContext().registerReceiver(
                customReceiver,
                customFilter,
                Context.RECEIVER_NOT_EXPORTED
            )
        }
    }

    override fun onStop() {
        super.onStop()

        // Always unregister to prevent leaks; guard with try/catch
        try {
            requireContext().unregisterReceiver(airplaneModeReceiver)
        } catch (_: IllegalArgumentException) {
        }

        try {
            requireContext().unregisterReceiver(customReceiver)
        } catch (_: IllegalArgumentException) {
        }
    }

    private fun sendCustomBroadcast() {
        val intent = Intent(CUSTOM_ACTION)
        intent.putExtra("message", "Hello from custom broadcast!")
        // Restrict the broadcast to this app package to avoid lint warning and accidental exposure
        intent.setPackage(requireContext().packageName)
        requireContext().sendBroadcast(intent)
    }
}
