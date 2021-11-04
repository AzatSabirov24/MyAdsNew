package com.azat_sabirov.myAdsNew.utils

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.azat_sabirov.myAdsNew.act.EditAdsAct
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    const val REQUEST_CODE_GET_IMAGES = 999
    const val REQUEST_CODE_GET_SINGLE_IMAGES = 998
    private fun getOptions(imageCounter: Int): Options {
        val options = Options.init()
            .setCount(imageCounter)                                                   //Number of images to restict selection count
            .setFrontfacing(false)                                         //Front Facing camera on start
            .setMode(Options.Mode.Picture)                                     //Option to select only pictures or videos or both
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
            .setPath("/pix/images")                                            //Custom Path For media Storage
        return options
    }

    fun launcher(
        context: EditAdsAct,
        launcher: ActivityResultLauncher<Intent>?,
        imageCounter: Int
    ) {
        PermUtil.checkForCamaraWritePermissions(context) {
            val intent = Intent(context, Pix::class.java).apply {
                putExtra("options", getOptions(imageCounter))
            }
            launcher?.launch(intent)
        }
    }

    fun getLauncherForMultiSelectImages(context: EditAdsAct): ActivityResultLauncher<Intent> {
        return context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val returnValues = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                    if (returnValues?.size!! > 1 && context.chooseImageItem == null) {

                        context.openChooseImageFrag(returnValues)

                    } else if (returnValues.size == 1 && context.chooseImageItem == null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val bitMapArray =
                                ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
                            context.imageAdapter.update(bitMapArray)
                        }
                    } else if (context.chooseImageItem != null) {
                        context.chooseImageItem?.updateAdapter(returnValues)
                    }
                }
            }
        }
    }

    fun getLauncherForSingleImage(context: EditAdsAct): ActivityResultLauncher<Intent> {
        return context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.let {
                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    context.chooseImageItem?.editSingleImage(uris?.get(0)!!, context.editPos)
                }
            }
        }
    }
}
