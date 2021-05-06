package com.azat_sabirov.myAdsNew.frag

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
import com.azat_sabirov.myAdsNew.utils.ImagePicker
import com.azat_sabirov.myAdsNew.utils.ItemTouchMoveCallback

class ImageFrag(
    private val fragmentCLoseInterface: FragCLoseInterface,
    private val newList: ArrayList<String>
) : Fragment() {
    val adapter = FragRvAdapter()
    val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    lateinit var rootElement: ListImageFragBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootElement = ListImageFragBinding.inflate(inflater)
        return rootElement.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        touchHelper.attachToRecyclerView(rootElement.rcImageItem)
        rootElement.rcImageItem.layoutManager = LinearLayoutManager(activity)
        rootElement.rcImageItem.adapter = adapter
        val updateList = ArrayList<SelectRvItem>()
        for (n in 0 until newList.size) {
            updateList.add(SelectRvItem(n.toString(), newList[n]))
        }
        adapter.updateAdapter(updateList,true)
    }

    override fun onDetach() {
        super.onDetach()
        fragmentCLoseInterface.onFragClose(adapter.mainArray)
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
            ImagePicker.getImages(activity as AppCompatActivity, imageCount)
            true
        }

        deleteImageItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(),true)
            true
        }
    }

    fun updateAdapter(newList: ArrayList<String>){
        val updateList = ArrayList<SelectRvItem>()
        for (n in adapter.mainArray.size until ImagePicker.MAX_IMAGE_COUNT) {
            updateList.add(SelectRvItem(n.toString(), newList[n - adapter.mainArray.size]))
        }
        adapter.updateAdapter(updateList,false)
    }
}