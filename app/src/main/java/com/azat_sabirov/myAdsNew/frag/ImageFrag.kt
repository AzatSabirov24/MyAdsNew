package com.azat_sabirov.myAdsNew.frag

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.databinding.ListImageFragBinding
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
) : Fragment() {
    val adapter = SelectFragRvAdapter()
    private val dragCallback = ItemTouchMoveCallback(adapter)
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
        if (newList != null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                val bitmapList = ImageManager.imageResize(newList)
                adapter.updateAdapter(bitmapList, true)
            }
        }
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>){
        adapter.updateAdapter(bitmapList, true)
    }
    override fun onDetach() {
        super.onDetach()
        fragmentCLoseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    private fun setUpToolbar() {
        rootElement.tb.inflateMenu(R.menu.menu_choose_item)

        val addImageItem = rootElement.tb.menu.findItem(R.id.id_add_item)
        val deleteImageItem = rootElement.tb.menu.findItem(R.id.id_delete_item)

        rootElement.tb.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        addImageItem.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.getImages(
                activity as AppCompatActivity,
                imageCount,
                ImagePicker.REQUEST_CODE_GET_IMAGES
            )
            true
        }

        deleteImageItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true)
            true
        }
    }

    fun updateAdapter(newList: ArrayList<String>) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.imageResize(newList)
            adapter.updateAdapter(bitmapList, false)
        }

    }

    fun editSingleImage(uri: String, pos: Int) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.imageResize(listOf(uri))
            adapter.mainArray[pos] = bitmapList[0]
            adapter.notifyDataSetChanged()
        }

    }
}