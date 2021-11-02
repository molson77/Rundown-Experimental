package com.example.rundown_experimental.ui.media

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rundown_experimental.R
import com.example.rundown_experimental.model.Article
import com.squareup.picasso.Picasso

class MediaListAdapter(private val listener: OnArticleListener): RecyclerView.Adapter<MediaListAdapter.MediaViewHolder>() {

    private var myList = emptyList<Article>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media_article, parent, false))
    }

    override fun onBindViewHolder(holder: MediaListAdapter.MediaViewHolder, position: Int) {

        val headline: String = myList[position].title
        val source: String = myList[position].source.name
        val date: String = myList[position].publishedAt
        val urlImg: String = myList[position].urlToImage

        val picasso = Picasso.get()

        holder.headline.text = headline
        holder.source.text = source
        holder.date.text = date.take(10)
        picasso.load(urlImg).into(holder.img)

    }

    override fun getItemCount(): Int {
        return myList.size
    }

    fun setData(newList: List<Article>) {
        myList = newList
        notifyDataSetChanged()
    }

    inner class MediaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
        val headline = itemView.findViewById<TextView>(R.id.media_item_headline)
        val source = itemView.findViewById<TextView>(R.id.media_item_source)
        val date = itemView.findViewById<TextView>(R.id.media_item_date)
        val img = itemView.findViewById<ImageView>(R.id.media_item_image)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener.onArticleClick(adapterPosition)
        }
    }

    interface OnArticleListener {
        fun onArticleClick(position: Int)
    }

}