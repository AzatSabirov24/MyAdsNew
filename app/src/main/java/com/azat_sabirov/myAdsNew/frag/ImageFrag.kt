package com.azat_sabirov.myAdsNew.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azat_sabirov.myAdsNew.R

class ImageFrag(
    private val fragmentCLoseInterface: FragCLoseInterface,
    private val newList: ArrayList<String>
) : Fragment() {
    val adapter = FragRvAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_image_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bBack = view.findViewById<Button>(R.id.bBack)
        bBack.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
        val rcImageItem = view.findViewById<RecyclerView>(R.id.rcImageItem)
        rcImageItem.layoutManager = LinearLayoutManager(activity)
        rcImageItem.adapter = adapter
        val updateList = ArrayList<SelectRvItem>()
        for (n in 0 until newList.size) {
            updateList.add(SelectRvItem(n.toString(), newList[n]))
        }
        adapter.updateAdapter(updateList)
    }

    override fun onDetach() {
        super.onDetach()
        fragmentCLoseInterface.onFragClose()
    }


}