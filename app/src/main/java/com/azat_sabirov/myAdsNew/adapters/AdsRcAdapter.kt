package com.azat_sabirov.myAdsNew.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azat_sabirov.myAdsNew.act.EditAdsAct
import com.azat_sabirov.myAdsNew.act.MainActivity
import com.azat_sabirov.myAdsNew.databinding.AdListItemBinding
import com.azat_sabirov.myAdsNew.model.Ad
import com.google.firebase.auth.FirebaseAuth

class AdsRcAdapter(val act: MainActivity) : RecyclerView.Adapter<AdsRcAdapter.AdsVH>() {
    val adsArray = mutableListOf<Ad>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsVH {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdsVH(binding, act)
    }

    override fun onBindViewHolder(holder: AdsVH, position: Int) {
        holder.setData(adsArray[position])
    }

    override fun getItemCount(): Int {
        return adsArray.size
    }

    fun updateAdapter(newList: List<Ad>) {
        adsArray.apply {
            clear()
            addAll(newList)
            notifyDataSetChanged()
        }
    }

    class AdsVH(val binding: AdListItemBinding, val act: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(ad: Ad) = with(binding) {
            ad.apply {
                descTv.text = desc
                priceTv.text = price
                titleTv.text = title
            }
            showEditPanel(isOwner(ad))
            editAdIb.setOnClickListener(onClickEdit(ad))
        }


    private fun onClickEdit(ad: Ad): View.OnClickListener {
        return View.OnClickListener {
            val editIntent = Intent(act, EditAdsAct::class.java).apply {
                putExtra(MainActivity.EDIT_STATE, true)
                putExtra(MainActivity.ADS_DATA, ad)
            }
            act.startActivity(editIntent)
        }
    }


    private fun isOwner(ad: Ad): Boolean {
        return ad.uid == act.mAuth.uid
    }

    private fun showEditPanel(isOwner: Boolean) = with(binding) {
        if (isOwner) editPanel.visibility = View.VISIBLE
        else editPanel.visibility = View.GONE
    }
}
}