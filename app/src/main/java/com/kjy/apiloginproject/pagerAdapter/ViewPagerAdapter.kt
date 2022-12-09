package com.kjy.apiloginproject.pagerAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kjy.apiloginproject.databinding.PagerAdListBinding

class ViewPagerAdapter: RecyclerView.Adapter<PagerHolder>() {

    var adList = mutableListOf<AdImage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerHolder {
        val binding = PagerAdListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerHolder(binding)
    }

    override fun onBindViewHolder(holder: PagerHolder, position: Int) {
        val adData = adList[position % 3]
        holder.setView(adData)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}

class PagerHolder(val binding: PagerAdListBinding): RecyclerView.ViewHolder(binding.root) {
    fun setView(adImage: AdImage) {
        Glide.with(itemView)
            .load(adImage.image)
            .into(binding.adImage)
    }
}