package com.azat_sabirov.myAdsNew.act

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.azat_sabirov.myAdsNew.databinding.ActivityEditAdsBinding
import com.azat_sabirov.myAdsNew.dialogs.DialogSpinnerHelper
import com.azat_sabirov.myAdsNew.utils.CityHelper

class EditAdsAct : AppCompatActivity() {
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)

    }

    //On Clicks

    fun onClickSelectCountry(view: View) {
        val listCountries = CityHelper.getAllCountries(this)
        dialog.showDialogSpinner(this, listCountries)
    }
}