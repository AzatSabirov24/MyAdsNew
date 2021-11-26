package com.azat_sabirov.myAdsNew.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azat_sabirov.myAdsNew.model.Ad
import com.azat_sabirov.myAdsNew.model.DBManager

class FirebaseViewModel : ViewModel() {
    private val dbManager = DBManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()

    fun loadAllAds() {
        dbManager.getAllAds(object : DBManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun adViewed(ad: Ad) {
        dbManager.adViewed(ad)
    }

    fun loadMyAds() {
        dbManager.getMyAds(object : DBManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun onFavClick(ad: Ad) {
        dbManager.onFavClick(ad, object : DBManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                val pos = updatedList?.indexOf(ad)
                if (pos != -1) {
                    pos?.let {
                        val favCounter = if (ad.isFav) ad.favCounter.toInt() - 1 else ad.favCounter.toInt() + 1
                        updatedList[pos] = updatedList[pos].copy(isFav = !ad.isFav, favCounter = favCounter.toString())
                    }
                }
                liveAdsData.postValue(updatedList)
            }
        })
    }

    fun deleteItem(ad: Ad) {
        dbManager.deleteAd(ad, object : DBManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                updatedList?.remove(ad)
                liveAdsData.postValue(updatedList)
            }
        })
    }
}