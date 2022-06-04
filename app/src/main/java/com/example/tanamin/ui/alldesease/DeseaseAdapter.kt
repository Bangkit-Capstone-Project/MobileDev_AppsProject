package com.example.tanamin.ui.alldesease

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tanamin.databinding.ItemAllDeseaseBinding
import com.example.tanamin.nonui.response.AllDeseaseResponse

class DeseaseAdapter  : RecyclerView.Adapter<DeseaseAdapter.ListViewHolder>(){
    private val listDeseases =  ArrayList<AllDeseaseResponse.DiseasesItem>()
    private lateinit var onItemClick: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClick = onItemClickCallback
    }
    interface OnItemClickCallback {
        fun onItemClicked(item: AllDeseaseResponse.DiseasesItem)
    }
    fun setListDesease(Desease: List<AllDeseaseResponse.DiseasesItem>){
        listDeseases.clear()
        listDeseases.addAll(Desease)
        notifyDataSetChanged()
    }
    class ListViewHolder (var binding: ItemAllDeseaseBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(diseasesItem: AllDeseaseResponse.DiseasesItem){
            binding.apply {
                Glide.with(itemView)
                    .load(diseasesItem.imageUrl)
                    .into(imgDeseases)
                tvDeseasesName.text = diseasesItem.name
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
       val binding = ItemAllDeseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listDeseases[position])
        holder.itemView.setOnClickListener {
            onItemClick.onItemClicked(listDeseases[holder.adapterPosition])
        }

    }

    override fun getItemCount(): Int = listDeseases.size
}