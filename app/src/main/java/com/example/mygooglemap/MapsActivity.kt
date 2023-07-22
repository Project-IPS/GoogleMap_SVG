package com.example.mygooglemap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Transformations.map
import com.example.mygooglemap.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val tag = "googleMap"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//
        val bitmap = getBitmapFromVectorDrawable(this, R.drawable.h18_floor_9)
        val icon = BitmapDescriptorFactory.fromBitmap(bitmap!!)
//        mMap.addMarker(
//            MarkerOptions()
//                .position(LatLng(-34.0, 151.0)) // Replace with your coordinates
//                .icon(icon)
//        )

        val buildingBounds = LatLngBounds(
            LatLng(19.13603403088429, 72.9091147880809),   // South west corner
            LatLng(19.136995862076922, 72.90953245746317))  // North east corner

        val groundOverlayOptions = GroundOverlayOptions()
            .positionFromBounds(buildingBounds)
            .image(icon)
            .bearing(9.1F)


//        Log.d(tag, "widht")
        mMap.addGroundOverlay(groundOverlayOptions)

        // Calculate the center point of your building
        val centerLat = (19.136078 + 19.136960) / 2
        val centerLng = (72.909051 + 72.909585) / 2
        val centerLatLng = LatLng(centerLat, centerLng)


        val blueDotCoord = convertToLatLng(60.0,150.0, 240.0, 600.0, Pair(19.136995862076922, 72.90953245746317), Pair(19.13603403088429, 72.9091147880809), 9.1F)
// Create a custom MarkerOptions object
        val markerOptions = MarkerOptions()
            .position(LatLng(blueDotCoord.first, blueDotCoord.second))
            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(this, R.drawable.blue_dot)!!))

        mMap.addMarker(markerOptions)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 19f))

    }

    fun convertToLatLng(x: Double, y: Double, width: Double, height: Double, latLngTopRight: Pair<Double, Double>, latLngBottomLeft: Pair<Double, Double>, rotation: Float): Pair<Double, Double> {
        // convert point to relative to center of rectangle
        val centerX = width / 2.0
        val centerY = height / 2.0
        val xRelCenter = x - centerX
        val yRelCenter = centerY - y // flip y-coordinate

        val (latTopRight, lngTopRight) = latLngTopRight
        val (latBottomLeft, lngBottomLeft) = latLngBottomLeft

// reverse rotation
        val bearingRad = -rotation * Math.PI / 180 // convert bearing to radians and reverse direction
        val xRotated = Math.cos(bearingRad) * xRelCenter - Math.sin(bearingRad) * yRelCenter
        val yRotated = Math.sin(bearingRad) * xRelCenter + Math.cos(bearingRad) * yRelCenter

// convert rotated point back to top-left-relative
        val xFinal = centerX + xRotated
        val yFinal = centerY + yRotated

// convert to relative position within rectangle
        val relativeX = xFinal / width
        val relativeY = yFinal / height

// convert to LatLng coordinates
        val lat = latBottomLeft + (latTopRight - latBottomLeft) * relativeY
        val lng = lngBottomLeft + (lngTopRight - lngBottomLeft) * relativeX

        return Pair(lat, lng)
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else{
            val bitmap = drawable?.let {
                Bitmap.createBitmap(
                    it.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888
                )
            }
            val canvas = bitmap?.let { Canvas(it) }
            if (drawable != null) {
                if (canvas != null) {
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
                }
            }
            if (drawable != null) {
                if (canvas != null) {
                    drawable.draw(canvas)
                }
            }
            bitmap
        }
    }



}