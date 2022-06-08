package com.example.tanamin.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tanamin.databinding.ItemAllDeseaseBinding
import com.example.tanamin.databinding.ItemHistoryBinding
import com.example.tanamin.nonui.data.Diseases
import com.example.tanamin.nonui.data.History
import com.example.tanamin.ui.alldesease.DeseaseAdapter
import com.example.tanamin.ui.news.NewsAdapter

class HistoryAdapter(private val listHistory: ArrayList<History>):  RecyclerView.Adapter<HistoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: HistoryAdapter.OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(var binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val(id, plantName, diseasesName, diseasesDescription, accuracy, imageUrl, createdAt) = listHistory[position]
        Glide.with(holder.itemView.context).load(imageUrl).into(holder.binding.imgDeseases)
        holder.binding.tvAccuracy.text = accuracy
        holder.itemView.setOnClickListener{ onItemClickCallback.onItemClicked(listHistory[holder.adapterPosition])}
    }

    override fun getItemCount(): Int {
        return listHistory.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: History)
    }
}