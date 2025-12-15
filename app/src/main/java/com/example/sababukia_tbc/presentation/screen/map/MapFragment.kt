package com.example.sababukia_tbc.presentation.screen.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentMapBinding
import com.example.sababukia_tbc.domain.model.Location
import com.example.sababukia_tbc.domain.model.UserLocation
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.mapper.toLocationUiModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.common.api.ResolvableApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(FragmentMapBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var userCurrentLocation: UserLocation? = null
    private var clusterManager: ClusterManager<LocationClusterItem>? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            ensureLocationSettings()
        } else {
            showSnackbar(getString(R.string.location_permission_denied))
        }
    }

    private val resolutionLauncher = registerForActivityResult(
        StartIntentSenderForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            enableMyLocation()
            viewModel.onEvent(MapViewModel.Event.GetCurrentLocation)
            viewModel.onEvent(MapViewModel.Event.RefreshLocations)
        } else {
            showLocationSettingsSnackbar()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupButtons()
        observeViewModel()
    }

    private fun setupButtons() {
        binding.fabAddLocation.setOnClickListener {
            userCurrentLocation?.let { location ->
                showAddLocationDialog(LatLng(location.latitude, location.longitude))
            } ?: showSnackbar(getString(R.string.location_not_available))
        }

        binding.fabMyLocation.setOnClickListener {
            userCurrentLocation?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            } ?: run {
                viewModel.onEvent(MapViewModel.Event.GetCurrentLocationManual)
                showSnackbar(getString(R.string.getting_location))
            }
        }

        binding.fabShowAll.setOnClickListener {
            showAllLocations()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    handleLocationsState(state.locationsState)
                    handleUserLocationState(state.userLocationState)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { effect ->
                    handleSideEffect(effect)
                }
            }
        }
    }

    private fun handleLocationsState(state: MapViewModel.LocationsState) {
        when (state) {
            is MapViewModel.LocationsState.Success -> {
                binding.progressBar.visibility = View.GONE
                displayLocations(state.locations)
            }
            is MapViewModel.LocationsState.Error -> {
                binding.progressBar.visibility = View.GONE
                showSnackbar(state.message.asString(requireContext()))
            }
            MapViewModel.LocationsState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun handleUserLocationState(state: MapViewModel.UserLocationState) {
        when (state) {
            is MapViewModel.UserLocationState.Success -> {
                userCurrentLocation = state.location
                val latLng = LatLng(state.location.latitude, state.location.longitude)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
            is MapViewModel.UserLocationState.Error -> {
                showSnackbar(state.message.asString(requireContext()))
            }
            MapViewModel.UserLocationState.Loading -> {}
        }
    }

    private fun handleSideEffect(effect: MapViewModel.SideEffect) {
        when (effect) {
            is MapViewModel.SideEffect.ShowError -> {
                showSnackbar(effect.message.asString(requireContext()), Snackbar.LENGTH_LONG)
            }
            is MapViewModel.SideEffect.ShowSuccess -> {
                showSnackbar(effect.message.asString(requireContext()))
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        setupClusterManager()
        setupMapListeners()
        setupMapUI()

        checkLocationPermission()
        viewModel.onEvent(MapViewModel.Event.RefreshLocations)
    }

    private fun setupClusterManager() {
        val map = googleMap ?: return
        val ctx = context ?: return

        clusterManager = ClusterManager<LocationClusterItem>(ctx, map).apply {

            // Custom renderer (optional)
            renderer = CustomClusterRenderer(ctx, map, this)

            // Handle cluster click - zoom in
            setOnClusterClickListener { cluster ->
                val newZoom = (map.cameraPosition.zoom) + 2f
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.position, newZoom))
                true
            }

            // Handle individual marker click - show bottom sheet
            setOnClusterItemClickListener { item ->
                val location = item.getLocation()
                LocationDetailsBottomSheet.newInstance(location)
                    .show(childFragmentManager, "LocationDetails")
                true
            }

            // Handle info window click (optional)
            setOnClusterItemInfoWindowClickListener { item ->
                val location = item.getLocation()
                LocationDetailsBottomSheet.newInstance(location)
                    .show(childFragmentManager, "LocationDetails")
            }
        }
    }

    private fun setupMapListeners() {
        val map = googleMap ?: return

        // Combined camera idle listener
        map.setOnCameraIdleListener {
            // Your custom camera idle logic here (if any)
            onCameraIdle()

            // Notify cluster manager
            clusterManager?.onCameraIdle()
        }

        // Combined marker click listener
        map.setOnMarkerClickListener { marker ->
            // Your custom marker click logic here (if any)
            val handled = onCustomMarkerClick(marker)

            if (!handled) {
                // Let cluster manager handle it
                clusterManager?.onMarkerClick(marker) ?: false
            } else {
                true
            }
        }

        // Combined info window click listener
        map.setOnInfoWindowClickListener { marker ->
            // Your custom info window logic here (if any)
            onCustomInfoWindowClick(marker)

            // Notify cluster manager
            clusterManager?.onInfoWindowClick(marker)
        }

        // Long click to add location
        map.setOnMapLongClickListener { latLng ->
            showAddLocationDialog(latLng)
        }
    }

    private fun onCameraIdle() {
        // Add your custom camera idle logic here
        // For example: load more data, update UI, etc.
    }

    private fun onCustomMarkerClick(marker: com.google.android.gms.maps.model.Marker): Boolean {
        // Add your custom marker click logic here
        // Return true if handled, false to let cluster manager handle
        return false
    }

    private fun onCustomInfoWindowClick(marker: com.google.android.gms.maps.model.Marker) {
        // Add your custom info window click logic here
    }

    private fun setupMapUI() {
        googleMap?.uiSettings?.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isMyLocationButtonEnabled = false // We have custom FAB
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                ensureLocationSettings()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.location_permission_title)
            .setMessage(R.string.location_permission_rationale)
            .setPositiveButton(R.string.ok) { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
        }
    }

    private fun displayLocations(locations: List<Location>) {
        clusterManager?.apply {
            clearItems()

            locations.forEach { location ->
                val locationUiModel = location.toLocationUiModel()
                val clusterItem = LocationClusterItem(locationUiModel)
                addItem(clusterItem)
            }

            cluster()
        }
    }

    private fun showAddLocationDialog(latLng: LatLng) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_location, null)
        val nameEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etLocationName)
        val descriptionEditText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etLocationDescription)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_location)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = nameEditText.text.toString()
                val description = descriptionEditText.text.toString()

                if (name.isNotBlank()) {
                    val newLocation = Location(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        description = description,
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )
                    viewModel.onEvent(MapViewModel.Event.SaveLocation(newLocation))
                } else {
                    showSnackbar(getString(R.string.name_required))
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showAllLocations() {
        val items = clusterManager?.algorithm?.items

        if (items.isNullOrEmpty()) {
            showSnackbar(getString(R.string.location_not_available))
            return
        }

        try {
            val builder = LatLngBounds.Builder()
            items.forEach { item ->
                builder.include(item.position)
            }

            val bounds = builder.build()
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (e: IllegalStateException) {
            showSnackbar(getString(R.string.location_not_available))
        }
    }

    private fun ensureLocationSettings() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L
        ).setMinUpdateIntervalMillis(5000L).build()

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                enableMyLocation()
                viewModel.onEvent(MapViewModel.Event.GetCurrentLocation)
                viewModel.onEvent(MapViewModel.Event.RefreshLocations)
            }
            .addOnFailureListener { exception ->
                handleLocationSettingsFailure(exception)
            }
    }

    private fun handleLocationSettingsFailure(exception: Exception) {
        if (exception is ResolvableApiException) {
            try {
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                resolutionLauncher.launch(intentSenderRequest)
            } catch (e: Exception) {
                showSnackbar(getString(R.string.location_required_enable))
            }
        } else {
            showSnackbar(getString(R.string.location_required_enable))
        }
    }

    private fun showLocationSettingsSnackbar() {
        Snackbar.make(
            binding.root,
            getString(R.string.location_required_enable),
            Snackbar.LENGTH_LONG
        ).setAction(R.string.settings) {
            val intent = android.content.Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            ).apply {
                data = android.net.Uri.fromParts("package", requireContext().packageName, null)
            }
            startActivity(intent)
        }.show()
    }

    private fun showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.root, message, duration).show()
    }

    override fun onDestroyView() {
        clusterManager?.clearItems()
        clusterManager = null
        googleMap = null
        super.onDestroyView()
    }
}