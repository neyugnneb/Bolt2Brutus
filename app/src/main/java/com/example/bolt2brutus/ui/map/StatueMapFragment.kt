package com.example.bolt2brutus.ui.map

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import androidx.fragment.app.viewModels
import com.example.bolt2brutus.R
import com.example.bolt2brutus.viewmodel.StatueViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class StatueMapFragment : Fragment(R.layout.fragment_statue_map) {
    private val statueViewModel: StatueViewModel by viewModels()

    private var map: GoogleMap? = null
    private var dialog: AlertDialog? = null
    private var sites: List<StatueSite> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.mapStatuesButton).setOnClickListener { browseStatues() }

        statueViewModel.statues.observe(viewLifecycleOwner) { statues ->
            sites = statues.toSites()
            showMarkers()
        }
        statueViewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                statueViewModel.messageShown()
            }
        }

        if (getString(R.string.maps_api_key).isBlank()) {
            showMapStatus(view, R.string.map_missing_key)
            return
        }
        val playServicesStatus = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(requireContext())
        if (playServicesStatus != ConnectionResult.SUCCESS) {
            showMapStatus(view, R.string.map_play_services_unavailable)
            return
        }

        view.findViewById<View>(R.id.mapLocationNote).isVisible = true
        val existingMap = childFragmentManager.findFragmentById(R.id.statueMapContainer) as? SupportMapFragment
        val mapFragment = existingMap
            ?: SupportMapFragment.newInstance(GoogleMapOptions().useViewLifecycleInFragment(true)).also {
                childFragmentManager.commitNow { add(R.id.statueMapContainer, it) }
            }
        mapFragment.getMapAsync { googleMap ->
            if (this.view !== view) return@getMapAsync
            map = googleMap
            googleMap.setOnMarkerClickListener { marker ->
                val site = marker.tag as? StatueSite ?: return@setOnMarkerClickListener false
                showSite(site)
                true
            }
            showMarkers()
            if (existingMap == null) {
                centerOnCampus(googleMap, view.findViewById(R.id.statueMapContainer))
            }
        }
    }

    override fun onDestroyView() {
        dialog?.dismiss()
        dialog = null
        map = null
        super.onDestroyView()
    }

    private fun showMarkers() {
        val googleMap = map ?: return
        googleMap.clear()
        sites.forEach { site ->
            googleMap.addMarker(MarkerOptions().position(site.position).title(site.name))?.tag = site
        }
    }

    private fun centerOnCampus(googleMap: GoogleMap, mapContainer: View) {
        mapContainer.doOnLayout { container ->
            if (map !== googleMap || container.width == 0 || container.height == 0) {
                return@doOnLayout
            }
            // Start on the main campus. The list can jump to venues beyond this initial view.
            val bounds = LatLngBounds(LatLng(39.9930, -83.0350), LatLng(40.0120, -83.0048))
            val padding = minOf(container.width, container.height) / 10
            val camera = CameraUpdateFactory.newLatLngBounds(bounds, container.width, container.height, padding)
            googleMap.moveCamera(camera)
        }
    }

    private fun browseStatues() {
        if (sites.isEmpty()) {
            Toast.makeText(requireContext(), R.string.map_statues_loading, Toast.LENGTH_SHORT).show()
            return
        }
        val entries = sites
            .flatMap { site -> site.statues.map { statue -> site to statue } }
            .sortedBy { (_, statue) -> statue.name }
        val labels = entries.map { (site, statue) ->
            getString(R.string.map_statue_at_site, statue.name, site.name)
        }
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.map_statues_title)
            .setItems(labels.toTypedArray()) { _, index ->
                val site = entries[index].first
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(site.position, SITE_ZOOM))
                showSite(site)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showSite(site: StatueSite) {
        val message = buildString {
            append(site.address)
            site.statues.forEach { statue ->
                append("\n\n").append(statue.name)
                if (statue.locationDescription.isNotBlank()) {
                    append('\n').append(statue.locationDescription)
                }
            }
            site.accessNote?.let { append("\n\n").append(getString(it)) }
        }
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(site.name)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun showMapStatus(view: View, @StringRes message: Int) {
        view.findViewById<View>(R.id.statueMapContainer).isVisible = false
        view.findViewById<TextView>(R.id.mapStatus).apply {
            setText(message)
            isVisible = true
        }
    }

    private companion object {
        const val SITE_ZOOM = 17f
    }
}
