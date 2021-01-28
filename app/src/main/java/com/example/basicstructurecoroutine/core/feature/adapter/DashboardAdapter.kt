package com.example.basicstructurecoroutine.core.feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.basicstructurecoroutine.R
import com.example.basicstructurecoroutine.core.model.response.YouTubeListResponseItem
import com.example.basicstructurecoroutine.databinding.DashboardItemBinding

/**
 * Created by Abhin.
 */
class DashboardAdapter(
    private var mList: ArrayList<YouTubeListResponseItem>,
    private val mItemClickListener: ItemClickListener
) : RecyclerView.Adapter<DashboardAdapter.CommonAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonAdapterViewHolder {
        return CommonAdapterViewHolder(DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_dashboard,
            parent,
            false
        ))
    }

    override fun getItemCount() = mList.size

    override fun onBindViewHolder(holder: CommonAdapterViewHolder, position: Int) {
        val banner = mList[position]
        holder.bindData(banner)
        holder.itemView.setOnClickListener {
            mItemClickListener.itemClick(position)
        }
    }

    class CommonAdapterViewHolder(var binding: DashboardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(list: YouTubeListResponseItem) = binding.apply {
            mData = list
            executePendingBindings()
        }
    }

    interface ItemClickListener {
        fun itemClick(position: Int)
    }
}
