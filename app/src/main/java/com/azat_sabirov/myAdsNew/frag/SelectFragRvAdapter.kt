package com.azat_sabirov.myAdsNew.frag

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azat_sabirov.myAdsNew.R
import com.azat_sabirov.myAdsNew.act.EditAdsAct
import com.azat_sabirov.myAdsNew.databinding.SelectImageFragItemBinding
import com.azat_sabirov.myAdsNew.utils.AdapterCallback
import com.azat_sabirov.myAdsNew.utils.ImageManager
import com.azat_sabirov.myAdsNew.utils.ImagePicker
import com.azat_sabirov.myAdsNew.utils.ItemTouchMoveCallback

class SelectFragRvAdapter(val adapterCallback: AdapterCallback) : RecyclerView.Adapter<SelectFragRvAdapter.ImageViewHolder>(),
    ItemTouchMoveCallback.ItemTouchAdapter {
    val mainArray = ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val viewBinding =
            SelectImageFragItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ImageViewHolder(viewBinding, parent.context, this)
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

    class ImageViewHolder(
        val viewBinding: SelectImageFragItemBinding,
        private val context: Context,
        val adapterSelect: SelectFragRvAdapter
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun setData(bitmap: Bitmap) = with(viewBinding) {
            tvTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            imageView.setImageBitmap(bitmap)
            imEditImage.setOnClickListener {
                ImagePicker.getImages(
                    context as EditAdsAct,
                    1,
                    ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGES
                )
                context.editPos = adapterPosition
            }
            imDelete.setOnClickListener {
                adapterSelect.mainArray.removeAt(adapterPosition)
                adapterSelect.notifyItemRemoved(adapterPosition)
                for (n in 0 until adapterSelect.mainArray.size) adapterSelect.notifyItemChanged(n)
                adapterSelect.adapterCallback.onItemDelete()
            }
        }
    }

    fun updateAdapter(newList: List<Bitmap>, needClearList: Boolean) {
        if (needClearList) mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }
}
