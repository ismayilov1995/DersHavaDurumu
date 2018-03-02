package com.example.ismayil.DersHavaDurumu

import android.graphics.PorterDuff
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

    var tvSpinner:TextView? = null

    var capitalArray = ArrayList<String>(5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        paytaxtlar()
        havaDurumu("Ankara")


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val selectedCity = p0?.getItemAtPosition(p2).toString()
        tvSpinner = p1 as TextView
        havaDurumu(selectedCity)

    }

    fun paytaxtlar(){
        val urlCapital = "http://country.io/capital.json"
        val paytaxtObje = JsonObjectRequest(Request.Method.GET, urlCapital, null, Listener<JSONObject?> {
            val allCitiesAd = it?.names()
            var paytaxt="Ankara"
            var paytaxtKod = "AA"

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

        }, Response.ErrorListener {
            Toast.makeText(this@MainActivity, "Siktirifsen", Toast.LENGTH_LONG).show()
        })
        MySingleton.getInstance(this)?.addToRequestQueue(paytaxtObje)
    }


    fun havaDurumu(cityURI: String) {
        val url = "http://api.openweathermap.org/data/2.5/weather?q=$cityURI&appid=abbcd2fcfec741ec783669c98b7f39d1&lang=tr&units=metric"
        val havaDurumObje = JsonObjectRequest(Request.Method.GET, url, null, Listener<JSONObject?> {

            tvDate.text = getDate()

            val main = it?.getJSONObject("main")
            val tempratur = main?.getInt("temp")
            tvTemp.text = tempratur.toString()

            val weather = it?.getJSONArray("weather")
            val description = weather?.getJSONObject(0)?.getString("description")
            tvDescription.text = description

            val icon = weather?.getJSONObject(0)?.getString("icon")
            val imageFilesName = resources.getIdentifier("icon_" + icon?.removeLastChar(), "drawable", packageName)
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
            Toast.makeText(this,"Secilen Sheher Barede Melumat Tapilmadi",Toast.LENGTH_LONG).show()
        })
        MySingleton.getInstance(this)?.addToRequestQueue(havaDurumObje)
    }

    fun getDate(): String {
        val calendar = Calendar.getInstance().time
        val formatting = SimpleDateFormat("EEEE, MMMM yyyy", Locale("az"))
        val date = formatting.format(calendar)

        return date
    }

}
private fun String.removeLastChar(): String {
    return this.substring(0,this.length-1)
}
