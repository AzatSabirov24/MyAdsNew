package com.azat_sabirov.myAdsNew.frag

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.act.EditAdsAct
import com.azat_sabirov.myAdsNew.databinding.ListImageFragBinding
import com.azat_sabirov.myAdsNew.dialogHelper.ProgressDialog
import com.azat_sabirov.myAdsNew.utils.AdapterCallback
import com.azat_sabirov.myAdsNew.utils.ImageManager
import com.azat_sabirov.myAdsNew.utils.ImagePicker
import com.azat_sabirov.myAdsNew.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageFrag(
    private val fragmentCLoseInterface: FragCLoseInterface,
    private val newList: ArrayList<String>?
) : Fragment(), AdapterCallback {
    val adapter = SelectFragRvAdapter(this)
    private val dragCallback = ItemTouchMoveCallback(adapter)
    private var addImageItem: MenuItem? = null
    val touchHelper = ItemTouchHelper(dragCallback)
    lateinit var rootElement: ListImageFragBinding
    var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootElement = ListImageFragBinding.inflate(inflater)
        return rootElement.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        touchHelper.attachToRecyclerView(rootElement.rcImageItem)
        rootElement.rcImageItem.layoutManager = LinearLayoutManager(activity)
        rootElement.rcImageItem.adapter = adapter
        if (newList != null) resizeSelectedImages(newList, true)
    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {
        super.onDetach()
        fragmentCLoseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    private fun resizeSelectedImages(newList: ArrayList<String>, needClear: Boolean) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(newList)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.mainArray.size > 2) addImageItem?.isVisible = false
        }
    }

    private fun setUpToolbar() {
        rootElement.tb.inflateMenu(R.menu.menu_choose_item)

        addImageItem = rootElement.tb.menu.findItem(R.id.id_add_item)
        val deleteImageItem = rootElement.tb.menu.findItem(R.id.id_delete_item)

        rootElement.tb.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        addImageItem?.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.launcher(
                activity as EditAdsAct,
                (activity as EditAdsAct).launcherMultiSelectImages,
                imageCount
            )
            true
        }

        deleteImageItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true)
            addImageItem?.isVisible = true
            true
        }
    }

    fun updateAdapter(newList: ArrayList<String>) {
        resizeSelectedImages(newList, false)
    }

    fun editSingleImage(uri: String, pos: Int) {
        val pBar = rootElement.rcImageItem[pos].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(listOf(uri))
            adapter.mainArray[pos] = bitmapList[0]
            adapter.notifyItemChanged(pos)
            pBar.visibility = View.GONE
        }
    }
}