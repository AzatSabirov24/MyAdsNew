package com.azat_sabirov.myAdsNew.act

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.adapters.ImageAdapter
import com.azat_sabirov.myAdsNew.databinding.ActivityEditAdsBinding
import com.azat_sabirov.myAdsNew.dialogs.DialogSpinnerHelper
import com.azat_sabirov.myAdsNew.frag.FragCLoseInterface
import com.azat_sabirov.myAdsNew.frag.ImageFrag
import com.azat_sabirov.myAdsNew.model.Ad
import com.azat_sabirov.myAdsNew.model.DBManager
import com.azat_sabirov.myAdsNew.utils.CityHelper
import com.azat_sabirov.myAdsNew.utils.ImagePicker
import com.fxn.utility.PermUtil


class EditAdsAct : AppCompatActivity(), FragCLoseInterface {
    var chooseImageItem: ImageFrag? = null
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    val imageAdapter = ImageAdapter()
    var editPos = 0
    private val dbManager = DBManager()
    var launcherMultiSelectImages: ActivityResultLauncher<Intent>? = null
    var launcherSingleSelectImage: ActivityResultLauncher<Intent>? = null
    private var isEditState = false
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        init()
        checkEditState()
    }

    private fun init() {
        rootElement.vpImages.adapter = imageAdapter
        launcherMultiSelectImages = ImagePicker.getLauncherForMultiSelectImages(this)
        launcherSingleSelectImage = ImagePicker.getLauncherForSingleImage(this)
    }

    private fun checkEditState() {
        isEditState = isEditState()
        if (isEditState) {
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad
            ad?.let { fillViews(ad!!) }
        }
    }

    private fun isEditState(): Boolean {
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun fillViews(ad: Ad) = with(rootElement) {
        ad.apply {
            tvCountry.text = country
            tvCities.text = city
            titleTelEt.setText(tel)
            withSendCheckBox.isChecked = withSend.toBoolean()
            tvCat.text = category
            titleEt.setText(title)
            titlePriceEt.setText(price)
            titleDescEt.setText(desc)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

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

    fun onClickSelectCat(view: View) {
        val listCategories = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showDialogSpinner(this, listCategories, rootElement.tvCat)
    }

    fun onClickGetImages(view: View) {
        if (imageAdapter.mainArray.size == 0) {
            ImagePicker.launcher(this, launcherMultiSelectImages, 3)
        } else {
            openChooseImageFrag(null)
            chooseImageItem?.updateAdapterFromEdit(imageAdapter.mainArray)
        }
    }

    fun onClickPublish(view: View) {
        val adTemp = fillAd()
        if (isEditState) {
            dbManager.publishAd(adTemp.copy(key = ad?.key), onPublishFinish())
        } else {
            dbManager.publishAd(adTemp, onPublishFinish())
        }
    }

    private fun onPublishFinish(): DBManager.FinishWorkListener{
        return object : DBManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }

    fun fillAd(): Ad = with(rootElement) {
        return Ad(
            tvCountry.text.toString(),
            tvCities.text.toString(),
            titleTelEt.text.toString(),
            withSendCheckBox.isChecked.toString(),
            tvCat.text.toString(),
            titleEt.text.toString(),
            titlePriceEt.text.toString(),
            titleDescEt.text.toString(),
            dbManager.db.push().key,
            dbManager.auth.uid
        )
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        rootElement.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageItem = null
    }

    fun openChooseImageFrag(newList: ArrayList<String>?) {
        chooseImageItem = ImageFrag(this, newList)
        rootElement.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageItem!!)
        fm.commit()
    }
}