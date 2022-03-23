package com.example.mappractice5

import android.accessibilityservice.AccessibilityService
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
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
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.String.format
import java.text.DateFormat
import java.text.MessageFormat.format
import java.util.*
import kotlin.collections.ArrayList

private lateinit var mMap: GoogleMap

private var size=0


class MainActivity : AppCompatActivity(), OnMapReadyCallback{

    private val screenShotButton:AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.screenShotButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        var v=window.decorView

        screenShotButton.setOnClickListener {
            val bitmap=getScreenShotFromView(v)
            if (bitmap!=null){
                saveMediaToStorage(bitmap)
            }
        }


    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }


    // this method saves the image to gallery
    private fun saveMediaToStorage(bitmap: Bitmap) {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Captured View and saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
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
            var positionX=""
            var positionY=""

            for(i in 17 until 34){
                positionX+=arr[0][i]
            }
            for(i in 39 until 56){
                positionY+=arr[0][i]
            }


            var position=LatLng(positionX.toDouble(),positionY.toDouble())
            googleMap.addMarker(MarkerOptions().position(position).title("좌표"). snippet("위도 : ${positionX}, 경도 : ${positionY}"))
        }


        mMap.setOnMapLongClickListener { latLng->
            val location = LatLng(latLng.latitude,latLng.longitude)
            googleMap.addMarker(MarkerOptions().position(location).title("좌표"). snippet("위도 : ${latLng.latitude}, 경도 : ${latLng.longitude}"))


            var current=locationArray(latLng.latitude.toString(),latLng.longitude.toString())
            var currentData= arrayListOf<locationArray>(current)

            setStringArrayPref(size.toString(),currentData)

            size+=1

            var arr= arrayListOf<String>(getStringArrayPref(size.toString()))

            Log.d("MainActivity","여기!!!!${arr[0][38]}+${arr[0][55]}///${arr[0]}")


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







