package com.hyperbyte.playlogtest.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hyperbyte.playlogtest.R

class CameraRollImagesAdapter(private val filePathList: ArrayList<String>) : RecyclerView.Adapter<CameraRollImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(filePathList[position])
    }

    override fun getItemCount(): Int {
        return filePathList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(uri: String) {
            Glide.with(itemView.context)
                .load(uri)
                .into(itemView.findViewById(R.id.image))
        }
    }
}