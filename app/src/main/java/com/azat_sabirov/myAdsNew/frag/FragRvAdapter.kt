package com.azat_sabirov.myAdsNew.frag

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.act.EditAdsAct
import com.azat_sabirov.myAdsNew.utils.ImagePicker
import com.azat_sabirov.myAdsNew.utils.ItemTouchMoveCallback

class FragRvAdapter : RecyclerView.Adapter<FragRvAdapter.ImageViewHolder>(),
    ItemTouchMoveCallback.ItemTouchAdapter {
    val mainArray = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_image_frag_item, parent, false)
        return ImageViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    override fun onMove(startPos: Int, targetPos: Int) {
        val itemTarget = mainArray[targetPos]
        mainArray[targetPos] = mainArray[startPos]
        mainArray[startPos] = itemTarget
        notifyItemMoved(startPos, targetPos)
    }

    override fun onClear() {
        notifyDataSetChanged()
    }

    class ImageViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvTitle: TextView
        lateinit var imageView: ImageView
        lateinit var imEditImage: ImageButton
        fun setData(item: String) {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            imageView = itemView.findViewById(R.id.imageView)
            tvTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            imageView.setImageURI(Uri.parse(item))
            imEditImage = itemView.findViewById(R.id.imEditImage)
            imEditImage.setOnClickListener {
               ImagePicker.getImages(context as EditAdsAct, 1, ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGES)
                context.editPos = adapterPosition
            }
        }
    }

    fun updateAdapter(newList: List<String>, needClearList: Boolean) {
        if (needClearList) mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }
}
