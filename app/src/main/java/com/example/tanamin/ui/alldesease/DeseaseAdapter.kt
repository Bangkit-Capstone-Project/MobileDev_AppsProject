package com.example.tanamin.ui.alldesease

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tanamin.databinding.ItemAllDeseaseBinding
import com.example.tanamin.nonui.data.Diseases
import com.example.tanamin.ui.news.NewsAdapter

class DeseaseAdapter(private val listDisease: ArrayList<Diseases>): RecyclerView.Adapter<DeseaseAdapter.ListViewHolder>(){
    class ListViewHolder(var binding: ItemAllDeseaseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemAllDeseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (id, name, description, imageUrl) = listDisease[position]

        Glide.with(holder.itemView.context).load(imageUrl).into(holder.binding.imgDeseases)
        holder.binding.tvDeseasesName.text = name

        val story = Diseases(id, name, description, imageUrl)
    }

    override fun getItemCount() = listDisease.size


}

