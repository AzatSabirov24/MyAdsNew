package com.azat_sabirov.myAdsNew.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

object CityHelper {

    fun getAllCountries(context: Context): ArrayList<String> {
        val temps = ArrayList<String>()

        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val countryNames = jsonObject.names()
            if (countryNames != null) {
                for (n in 0 until countryNames.length()) {
                    temps.add(countryNames.getString(n))
                }
            }
        } catch (e: IOException) {
        }
        return temps
    }
}