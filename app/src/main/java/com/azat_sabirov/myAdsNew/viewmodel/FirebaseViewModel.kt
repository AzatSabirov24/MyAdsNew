package com.azat_sabirov.myAdsNew.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azat_sabirov.myAdsNew.model.Ad
import com.azat_sabirov.myAdsNew.model.DBManager

class FirebaseViewModel: ViewModel() {
    private val dbManager = DBManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()

    fun loadAllAds() {
        dbManager.readDataFromDb(object : DBManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }
}