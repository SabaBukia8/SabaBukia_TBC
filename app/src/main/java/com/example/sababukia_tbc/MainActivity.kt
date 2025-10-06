package com.example.sababukia_tbc


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sababukia_tbc.databinding.ActivityMainBinding
import java.util.UUID

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var enterAnagram : AppCompatEditText
    lateinit var saveBtn : AppCompatButton
    lateinit var outputBtn : AppCompatButton
    lateinit var anagrams : AppCompatTextView
    lateinit var clearBtn : AppCompatButton
    lateinit var connectAndTransferBtn: AppCompatButton
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothLeScanner: BluetoothLeScanner

    private val anagramList = mutableListOf<String>()

    private var bluetoothGatt: BluetoothGatt? = null
    private var connectedDevice: BluetoothDevice? = null

    private val SERVICEUUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")
    private val CHARACTERISTICUUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb")
    private val discoveredDevices = mutableListOf<BluetoothDevice>()
    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())
    private val SCANPERIOD: Long = 10000

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            startBLEScan()
        } else {
            Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_LONG).show()
        }
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        enterAnagram = binding.enterAnagram
        saveBtn = binding.saveBtn
        outputBtn = binding.outputBtn
        anagrams = binding.anagrams
        clearBtn = binding.clearBtn
        connectAndTransferBtn = binding.connectAndTransferBtn

        saveBtn.text = getString(R.string.save_button)
        outputBtn.text = getString(R.string.output_button)
        clearBtn.text = getString(R.string.clear_button)
        enterAnagram.hint = getString(R.string.enter_anagram_hint)

        setupClickListeners()
        initializeBluetooth()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.saveBtn.setOnClickListener {
            saveAnagram()
        }

        binding.outputBtn.setOnClickListener {
            groupAnagrams()
        }

        binding.clearBtn.setOnClickListener {
            clearThePage()
        }

        binding.connectAndTransferBtn.setOnClickListener {
            connectAndTransfer()
        }
    }

    private fun initializeBluetooth() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.no_ble_support, Toast.LENGTH_LONG).show()
            connectAndTransferBtn.isEnabled = false
        }
    }

    private fun connectAndTransfer() {
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, R.string.bluetooth_disabled, Toast.LENGTH_LONG).show()
            return
        }

        if (!hasRequiredPermissions()) {
            requestPermissions()
            return
        }

        startBLEScan()
    }

    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        requestPermissionLauncher.launch(permissions)
    }

    @SuppressLint("MissingPermission")
    private fun startBLEScan() {
        discoveredDevices.clear()

        if (!hasRequiredPermissions()) {
            Toast.makeText(this, "Bluetooth permissions required", Toast.LENGTH_SHORT).show()
            return
        }

        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        Toast.makeText(this, R.string.scanning_for_devices, Toast.LENGTH_SHORT).show()

        scanning = true
        bluetoothLeScanner.startScan(bleScanCallback)

        handler.postDelayed({
            if (scanning) {
                scanning = false
                bluetoothLeScanner.stopScan(bleScanCallback)
                showDeviceSelectionDialog()
            }
        }, SCANPERIOD)
    }


    private val bleScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            val device = result.device
            if (discoveredDevices.none { it.address == device.address }) {
                discoveredDevices.add(device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            scanning = false
            Toast.makeText(this@MainActivity, "Scan failed with error: $errorCode", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showDeviceSelectionDialog() {
        if (discoveredDevices.isEmpty()) {
            Toast.makeText(this, "No BLE devices found", Toast.LENGTH_SHORT).show()
            return
        }

        val deviceNames = discoveredDevices.mapIndexed { index, device ->
            "${index + 1}. ${device.name ?: "Unknown Device"} (${device.address})"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select BLE Device to Connect")
            .setItems(deviceNames) { dialog, which ->
                connectToSelectedDevice(which)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun connectToSelectedDevice(index: Int) {
        if (index < discoveredDevices.size) {
            val selectedDevice = discoveredDevices[index]
            connectedDevice = selectedDevice
            Toast.makeText(this, "Connecting to ${selectedDevice.name ?: "Unknown"}", Toast.LENGTH_SHORT).show()
            connectToDevice(selectedDevice)
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        Toast.makeText(this, getString(R.string.connecting_to_device, device.name ?: "Unknown"), Toast.LENGTH_SHORT).show()
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Connected to GATT server
                runOnUiThread {
                    Toast.makeText(this@MainActivity, R.string.connection_successful, Toast.LENGTH_SHORT).show()
                }
                // Discover services
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Disconnected from GATT server
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Disconnected from device", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Services discovered, sending data...", Toast.LENGTH_SHORT).show()
                }
                sendDataToDevice()
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)

            runOnUiThread {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Toast.makeText(this@MainActivity, R.string.data_sent, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, R.string.data_failed, Toast.LENGTH_SHORT).show()
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    bluetoothGatt?.disconnect()
                }, 1000)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendDataToDevice() {
        val message = "I am a hacker from tbc it academy"
        val messageBytes = message.toByteArray(Charsets.UTF_8)

        val service = bluetoothGatt?.getService(SERVICEUUID)
        var characteristic = service?.getCharacteristic(CHARACTERISTICUUID)

        if (characteristic == null) {
            characteristic = findWritableCharacteristic()
        }

        if (characteristic != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bluetoothGatt?.writeCharacteristic(
                    characteristic,
                    messageBytes,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            } else {
                characteristic.value = messageBytes
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "No writable characteristic found", Toast.LENGTH_SHORT).show()
                bluetoothGatt?.disconnect()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun findWritableCharacteristic(): BluetoothGattCharacteristic? {
        bluetoothGatt?.services?.forEach { service ->
            service.characteristics.forEach { characteristic ->
                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0 ||
                    characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) {
                    return characteristic
                }
            }
        }
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        if (scanning) {
            bluetoothLeScanner.stopScan(bleScanCallback)
        }
    }
    private fun saveAnagram(){
        val input = binding.enterAnagram.text.toString().trim()

        if (input.isEmpty()) {
            enterAnagram.error = getString(R.string.error_empty_anagram)
            return
        }

        val checkForCopy = anagramList.any { it.equals(input, ignoreCase = true) }

        if (checkForCopy) {
            enterAnagram.error = getString(R.string.error_duplicate_word, input)
            Toast.makeText(this, getString(R.string.toast_word_exists, input), Toast.LENGTH_SHORT).show()
            return
        } else{
            anagramList.add(input)
            binding.enterAnagram.text?.clear()
            enterAnagram.error = null
            Toast.makeText(this, getString(R.string.success_word_added, input), Toast.LENGTH_SHORT).show()
        }
    }


    private fun groupAnagrams(){
        if (anagramList.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_words_saved, Toast.LENGTH_SHORT).show()
            return
        }

        val anagramGroups = groupingAnagrams(anagramList)

        binding.displayAnagrams.removeAllViews()

        anagramGroups.forEach { group ->
            if (group.size > 1) {
                addAnagrams(group)
            }
        }

        val groupCount = anagramGroups.count { it.size > 1 }
        binding.anagrams.text = getString(R.string.anagram_groups_count, groupCount)

        if (groupCount == 0) {
            Toast.makeText(this, R.string.toast_no_anagram_groups, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addAnagrams(groupOfAnagrams: List<String>) {
        val groupTextView = AppCompatTextView(this).apply {
            text = "[${groupOfAnagrams.joinToString(", ")}]"

        }
        binding.displayAnagrams.addView(groupTextView)
    }

    private fun groupingAnagrams(words: List<String>): List<List<String>> {
        val wordsInAnagram = words.toMutableList()
        val result = mutableListOf<List<String>>()

        while (wordsInAnagram.isNotEmpty()) {
            val targetWord = wordsInAnagram.removeAt(0)
            val thisGroup = mutableListOf(targetWord)

            var i = 0
            while (i < wordsInAnagram.size) {
                if (isAnAnagram(targetWord, wordsInAnagram[i])) {
                    thisGroup.add(wordsInAnagram.removeAt(i))
                } else {
                    i++
                }
            }

            result.add(thisGroup)
        }

        return result
    }

    private fun isAnAnagram(word1: String, word2: String): Boolean {
        if (word1.length != word2.length) return false

        val sorted1 = word1.lowercase().toCharArray().sorted().joinToString("")
        val sorted2 = word2.lowercase().toCharArray().sorted().joinToString("")

        return sorted1 == sorted2
    }
    private fun clearThePage(){
        anagramList.clear()
        binding.displayAnagrams.removeAllViews()
        binding.anagrams.text = getString(R.string.anagram_groups_count, 0)
    }


}