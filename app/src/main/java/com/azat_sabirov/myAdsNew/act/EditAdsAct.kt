package com.azat_sabirov.myAdsNew.act

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EditAdsAct : AppCompatActivity(), FragCLoseInterface {
    private var chooseImageItem: ImageFrag? = null
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    val imageAdapter = ImageAdapter()
    var editPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
        init()
    }

    private fun init() {

        rootElement.vpImages.adapter = imageAdapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES) {
            if (data != null) {
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                if (returnValues?.size!! > 1 && chooseImageItem == null) {

                    openChooseImageFrag(returnValues)

                } else if (returnValues.size == 1 && chooseImageItem == null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitMapArray = ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
                        imageAdapter.update(bitMapArray)
                    }
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

    fun onClickSelectCat(view: View) {
        val listCategories = resources.getStringArray(R.array.category).toMutableList() as ArrayList
        dialog.showDialogSpinner(this, listCategories, rootElement.tvCat)
    }

    fun onClickGetImages(view: View) {
        if (imageAdapter.mainArray.size == 0) {
            ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)
        } else {
            openChooseImageFrag(null)
            chooseImageItem?.updateAdapterFromEdit(imageAdapter.mainArray)
        }
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        rootElement.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageItem = null
    }

    private fun openChooseImageFrag(newList: ArrayList<String>?) {
        chooseImageItem = ImageFrag(this, newList)
        rootElement.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageItem!!)
        fm.commit()
    }
}