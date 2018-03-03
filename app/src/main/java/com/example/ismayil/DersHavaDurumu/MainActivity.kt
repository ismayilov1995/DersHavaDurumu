package com.example.ismayil.DersHavaDurumu

import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

    var tvSpinner:TextView? = null
    var capitalArray = ArrayList<String>(5)
    private var location:SimpleLocation? = null
    var longitude:String? = null
    var latitude:String? = null
    var url:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        capitalToSpinner()
        weatherForCities("Baku")




    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        tvSpinner = p1 as TextView

        if (p2 == 0){
            location = SimpleLocation(this)
            if (!location!!.hasLocationEnabled()){
                spinner.setSelection(245)
                Toast.makeText(this,"GPS -i Aktiv Et",Toast.LENGTH_SHORT).show()
                SimpleLocation.openSettings(this)
            }else{
                if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
                }else{
                    location = SimpleLocation(this)
                    latitude = String.format("%.6f",location?.latitude)
                    longitude = String.format("%.6f",location?.longitude)
                    weatherForLocation(latitude,longitude)
                }
            }
        }else{
            val selectedCity = p0?.getItemAtPosition(p2).toString()
            weatherForCities(selectedCity)
        }
    }

    private fun weatherForLocation(latitude: String?, longitude: String?) {
        url = "http://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=abbcd2fcfec741ec783669c98b7f39d1&lang=tr&units=metric"
        valleyListener(url!!)
    }

    private fun weatherForCities(cityURI: String) {
        url = "http://api.openweathermap.org/data/2.5/weather?q=$cityURI&appid=abbcd2fcfec741ec783669c98b7f39d1&lang=tr&units=metric"
        valleyListener(url!!)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                location = SimpleLocation(this)
                latitude = String.format("%.6f",location?.latitude)
                longitude = String.format("%.6f",location?.longitude)
                weatherForLocation(latitude,longitude)
            }else{
                spinner.setSelection(245)
                Toast.makeText(this,"Nolub aaa Icaze Verde...",Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun capitalToSpinner(){
        val urlCapital = "http://country.io/capital.json"
        val paytaxtObje = JsonObjectRequest(Request.Method.GET, urlCapital, null, Listener<JSONObject?> { it ->
            val allCitiesAd = it?.names()
            var paytaxt="Baku"
            var paytaxtKod = "AZ"

                capitalArray.add("Indiki Yer")
            for (i in 0 until allCitiesAd!!.length()){
                paytaxtKod = allCitiesAd[i].toString()
                paytaxt = it.get(paytaxtKod).toString()
                capitalArray.add(paytaxt)
            }
            val adapter = ArrayAdapter(this,R.layout.spinner_item_style_custom,capitalArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.background.setColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
            spinner.setSelection(245)

        }, Response.ErrorListener {
            Toast.makeText(this@MainActivity, "Siktirifsen", Toast.LENGTH_LONG).show()
        })
        MySingleton.getInstance(this)?.addToRequestQueue(paytaxtObje)
    }

    private fun valleyListener(url:String){
        var cityName:String?="Yuklenir"
        val havaDurumObje = JsonObjectRequest(Request.Method.GET, url, null, Listener<JSONObject?> {

            val main = it?.getJSONObject("main")
            val tempratur = main?.getInt("temp")
            val weather = it?.getJSONArray("weather")
            val description = weather?.getJSONObject(0)?.getString("description")
            val icon = weather?.getJSONObject(0)?.getString("icon")
            val imageFilesName = resources.getIdentifier("icon_" + icon?.removeLastChar(), "drawable", packageName)
            cityName = it?.getString("name")

            tvSpinner?.text = cityName
            tvDate.text = getDate()
            tvTemp.text = tempratur.toString()
            tvDescription.text = description

            if (icon?.last() == 'd') {
                spinner.background.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
                rootLayout.background = getDrawable(R.drawable.bg)
                tvDate.setTextColor(resources.getColor(R.color.black))
                tvDegree.setTextColor(resources.getColor(R.color.black))
                tvDescription.setTextColor(resources.getColor(R.color.black))
                tvTemp.setTextColor(resources.getColor(R.color.black))
                tvSpinner?.setTextColor(resources.getColor(R.color.black))
            } else {
                spinner.background.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
                rootLayout.background = getDrawable(R.drawable.gece)
                tvDate.setTextColor(resources.getColor(R.color.white))
                tvTemp.setTextColor(resources.getColor(R.color.white))
                tvDescription.setTextColor(resources.getColor(R.color.white))
                tvDegree.setTextColor(resources.getColor(R.color.white))
                tvSpinner?.setTextColor(resources.getColor(R.color.white))
            }
            ivSymbol.setImageResource(imageFilesName)
        }, Response.ErrorListener {
            spinner.setSelection(245)
            Toast.makeText(this,"Secilen Sheher Barede Melumat Tapilmadi",Toast.LENGTH_LONG).show()
        })
        MySingleton.getInstance(this)?.addToRequestQueue(havaDurumObje)
    }

    private fun getDate(): String {
        val calendar = Calendar.getInstance().time
        val formatting = SimpleDateFormat("EEEE, MMMM yyyy", Locale("az"))
        val date = formatting.format(calendar)

        return date
    }
}

private fun String.removeLastChar(): String {
    return this.substring(0,this.length-1)
}
