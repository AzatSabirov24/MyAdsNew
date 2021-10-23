package com.azat_sabirov.myAdsNew.db

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DBManager {
    val db = Firebase.database.reference

    fun publishAd(){
        db.setValue("Hi")
    }
}