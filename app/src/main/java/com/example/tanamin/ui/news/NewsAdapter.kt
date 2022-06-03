package com.example.tanamin.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tanamin.databinding.ItemNewsBinding
import com.example.tanamin.nonui.data.News

class NewsAdapter (private val listNews: ArrayList<News>) : RecyclerView.Adapter<NewsAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {

        fun onItemClicked(data: News)
    }

    class ListViewHolder (var binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (imgNews, newsTittle, newsDate, _ ) = listNews[position]
        holder.binding.tvNewsTittle.text = newsTittle
        holder.binding.tvNewsDate.text = newsDate
        holder.binding.imgNews.setImageResource(imgNews)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listNews[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listNews.size


}