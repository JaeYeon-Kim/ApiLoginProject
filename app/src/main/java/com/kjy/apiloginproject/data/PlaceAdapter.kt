package com.kjy.apiloginproject.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.kjy.apiloginproject.databinding.PlaceListBinding

class PlaceAdapter: RecyclerView.Adapter<Holder>() {

    var placeList = arrayListOf<ListLayout>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = PlaceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)

    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val placeData = placeList[position]
        holder.setPlace(placeData)
        // 홀더의 아이템뷰를 클릭했을때 그때의 이벤트
        // it = view, position은 itemView의 인덱스 위치
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }


    // 리사이클러뷰의 아이템 클릭을 위한 인터페이스 선언
    // 클릭리스너 설정
    interface OnItemClickListener : AdapterView.OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener

}

class Holder(val binding: PlaceListBinding): RecyclerView.ViewHolder(binding.root) {
    fun setPlace (listLayout: ListLayout) {
        binding.placeName.text = listLayout.name
        binding.placePhoneNumber.text = listLayout.phoneNumber
        binding.placeAddress.text = listLayout.address
    }
}



