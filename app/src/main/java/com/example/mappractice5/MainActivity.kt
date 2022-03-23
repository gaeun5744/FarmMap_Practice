package com.example.mappractice5

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.edit
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager

private lateinit var mMap: GoogleMap

private var size=0


class MainActivity : AppCompatActivity(), OnMapReadyCallback{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        fun setStringArrayPref(key: String, values: ArrayList<locationArray>) {
            val gson = Gson()
            val json = gson.toJson(values)
            val prefs = getSharedPreferences(size.toString(), Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(key, json)
            editor.apply()
        }

        fun getStringArrayPref(key: String): String {
            val prefs = getSharedPreferences(key, Context.MODE_PRIVATE)
            val json = prefs.getString(key, null)
            val gson = Gson()

            //val storedData=Gson().fromJson(json,Array<locationArray>::class.java).toList() as ArrayList<locationArray>

            val storedData: ArrayList<locationArray> = gson.fromJson(json,
                object : TypeToken<ArrayList<locationArray>>() {}.type
            )


            return storedData.toString()
        }

        var mMap = googleMap
        mMap.mapType=GoogleMap.MAP_TYPE_HYBRID

        for(i in 0 until size){
            var arr= arrayListOf<String>(getStringArrayPref(i.toString()))
            var position=LatLng(arr[0].toDouble(),arr[1].toDouble())
            googleMap.addMarker(MarkerOptions().position(position).title("좌표"). snippet("위도 : ${arr[0]}, 경도 : ${arr[1]}"))
        }


        mMap.setOnMapLongClickListener { latLng->
            val location = LatLng(latLng.latitude,latLng.longitude)
            googleMap.addMarker(MarkerOptions().position(location).title("좌표"). snippet("위도 : ${latLng.latitude}, 경도 : ${latLng.longitude}"))


            var current=locationArray(latLng.latitude.toString(),latLng.longitude.toString())
            var currentData= arrayListOf<locationArray>(current)

            setStringArrayPref(size.toString(),currentData)

            size+=1


            /*
            //저장하는 기능(READ)
            var size=0
            val preferences = getSharedPreferences("0",0)
            val jsonData=preferences.getString("0","")
            val gson=Gson()
            val token:TypeToken<MutableList<locationArray>> = object : TypeToken<MutableList<locationArray>>(){}
            val list:MutableList<locationArray>?=gson.fromJson(jsonData,token.type)

            //저장하는 기능(Write)
            if (list != null) {
                list.add(locationArray(latLng.latitude,latLng.longitude))
            }
            preferences.edit {
                putString("0",gson.toJson(list,token.type))
            }
             */
            //Toast.makeText(this, "${latLng.latitude},${latLng.longitude}",Toast.LENGTH_SHORT).show()
        }

    }

}







