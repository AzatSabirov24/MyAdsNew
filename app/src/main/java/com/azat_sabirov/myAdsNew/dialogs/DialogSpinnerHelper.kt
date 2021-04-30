package com.azat_sabirov.myAdsNew.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.utils.CityHelper

class DialogSpinnerHelper {

    fun showDialogSpinner(context: Context, list: ArrayList<String>){
        val builder = AlertDialog.Builder(context)
        val dialog= builder.create()
        val rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null)
        val adapter = RcViewDialogSpinnerAdapter(context, dialog)
        val rcView = rootView.findViewById<RecyclerView>(R.id.rcSpView)
        val svSpinner = rootView.findViewById<SearchView>(R.id.svSpinner)
        rcView.layoutManager = LinearLayoutManager(context)
        rcView.adapter = adapter
        dialog.setView(rootView)
        setFilterCountries(adapter, list, svSpinner)
        adapter.updateAdapter(list)
        dialog.show()
    }

    private fun setFilterCountries(adapter: RcViewDialogSpinnerAdapter, list: ArrayList<String>, svSpinner: SearchView?) {
        svSpinner?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
               return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempList = CityHelper.filterListData(list, newText)
                adapter.updateAdapter(tempList)
                return true
            }
        })
    }


}