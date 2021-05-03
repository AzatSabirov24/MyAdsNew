package com.azat_sabirov.myAdsNew.frag

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azat_sabirov.myAdsNew.R

class FragRvAdapter : RecyclerView.Adapter<FragRvAdapter.ImageViewHolder>() {
    private val mainArray = ArrayList<SelectRvItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
       holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
       return mainArray.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvTitle : TextView
        lateinit var imageView: ImageView
        fun setData(item: SelectRvItem){
            tvTitle = itemView.findViewById(R.id.tvTitle)
            imageView = itemView.findViewById(R.id.imageView)
            tvTitle.text = item.itemText
            imageView.setImageURI(Uri.parse(item.imageUri))
        }
    }

    fun updateAdapter(newList: List<SelectRvItem>){
        mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }
}
