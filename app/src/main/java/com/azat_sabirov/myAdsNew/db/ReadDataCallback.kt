package com.azat_sabirov.myAdsNew.db

import com.azat_sabirov.myAdsNew.data.Ad

interface ReadDataCallback {
    fun readData(list: List<Ad>)
}