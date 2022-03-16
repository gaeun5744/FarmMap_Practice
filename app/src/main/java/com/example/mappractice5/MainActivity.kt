package com.example.mappractice5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager

private lateinit var mMap: GoogleMap


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)



    }

    override fun onMapReady(googleMap: GoogleMap) {
        var mMap = googleMap
        mMap.mapType=GoogleMap.MAP_TYPE_HYBRID


        mMap.setOnMapLongClickListener { latLng->
            val location = LatLng(latLng.latitude,latLng.longitude)
            googleMap.addMarker(MarkerOptions().position(location).title("좌표"). snippet("위도 : ${latLng.latitude}, 경도 : ${latLng.longitude}"))


            //Toast.makeText(this, "${latLng.latitude},${latLng.longitude}",Toast.LENGTH_SHORT).show()
        }



    }



}



