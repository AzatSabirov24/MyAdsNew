package com.azat_sabirov.myAdsNew.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azat_sabirov.myAdsNew.data.Ad
import com.azat_sabirov.myAdsNew.databinding.AdListItemBinding

class AdsRcAdapter : RecyclerView.Adapter<AdsRcAdapter.AdsVH>() {
    val adsArray = mutableListOf<Ad>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsVH {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return AdsVH(binding)
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

    class AdsVH(val binding: AdListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(ad: Ad) = with(binding) {
            ad.apply {
                descTv.text = desc
                priceTv.text = price
            }
        }
    }
}