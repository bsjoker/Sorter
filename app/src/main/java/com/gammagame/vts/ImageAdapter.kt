package com.gammagame.vts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView

class ImageAdapter internal constructor(context: Context, private val resource: Int, private val itemList: ArrayList<Int>?) : ArrayAdapter<ImageAdapter.ItemHolder>(context, resource) {

    override fun getCount(): Int {
        return if (this.itemList != null) this.itemList.size else 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ItemHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null)
            holder = ItemHolder()
            holder.icon = convertView!!.findViewById(R.id.pic)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemHolder
        }

        holder.icon!!.setImageResource(this.itemList!![position])

        return convertView
    }

    class ItemHolder {
        var icon: ImageView? = null
    }
}
