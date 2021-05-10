package com.azat_sabirov.myAdsNew.act

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.adapters.ImageAdapter
import com.azat_sabirov.myAdsNew.databinding.ActivityEditAdsBinding
import com.azat_sabirov.myAdsNew.dialogs.DialogSpinnerHelper
import com.azat_sabirov.myAdsNew.frag.FragCLoseInterface
import com.azat_sabirov.myAdsNew.frag.ImageFrag
import com.azat_sabirov.myAdsNew.utils.CityHelper
import com.azat_sabirov.myAdsNew.utils.ImageManager
import com.azat_sabirov.myAdsNew.utils.ImagePicker
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil


class EditAdsAct : AppCompatActivity(), FragCLoseInterface {
    private var chooseImageItem: ImageFrag? = null
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    val adapter = ImageAdapter()
    var editPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        init()
    }

    private fun init() {

        rootElement.vpImages.adapter = adapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES) {
            if (data != null) {
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                if (returnValues?.size!! > 1 && chooseImageItem == null) {

                    openChooseImageFrag(returnValues)

                } else if (returnValues.size == 1 && chooseImageItem == null) {
//                    adapter.update(returnValues)
                    val tempList = ImageManager.getImageSize(returnValues[0])
                    /*Log.d("MyLog","Image width: ${tempList[0]}")
                    Log.d("MyLog","Image height: ${tempList[1]}")*/
                }

                else if (chooseImageItem != null) {
                    chooseImageItem?.updateAdapter(returnValues)
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGES){
            if (data != null) {
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                chooseImageItem?.editSingleImage(uris?.get(0)!!, editPos)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)
                } else {
                    Toast.makeText(
                        this,
                        "Approve permissions to open Pix ImagePicker",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    //On Clicks

    fun onClickSelectCountry(view: View) {
        val listCountries = CityHelper.getAllCountries(this)
        dialog.showDialogSpinner(this, listCountries, rootElement.tvCountry)
        if (rootElement.tvCities.toString() != getString(R.string.select_city)) {
            rootElement.tvCities.text = getString(R.string.select_city)
        }

    }

    fun onClickSelectCity(view: View) {
        val country: String = rootElement.tvCountry.text.toString()
        if (country != getString(R.string.select_country)) {
            val listCities = CityHelper.getAllCity(country, this)
            dialog.showDialogSpinner(this, listCities, rootElement.tvCities)
        } else Toast.makeText(this, R.string.select_country, Toast.LENGTH_LONG).show()
    }

    fun onClickGetImages(view: View) {
        if (adapter.mainArray.size > 0) {
            openChooseImageFrag(adapter.mainArray)
        } else {
            ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)
        }
    }

    override fun onFragClose(list: ArrayList<String>) {
        rootElement.scrollViewMain.visibility = View.VISIBLE
        adapter.update(list)
        chooseImageItem = null
    }

    private fun openChooseImageFrag(newList: ArrayList<String>) {
        chooseImageItem = ImageFrag(this, newList)
        rootElement.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageItem!!)
        fm.commit()
    }
}