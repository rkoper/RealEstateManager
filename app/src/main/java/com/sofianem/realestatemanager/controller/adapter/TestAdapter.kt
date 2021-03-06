package com.sofianem.realestatemanager.controller.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.sofianem.realestatemanager.R
import com.sofianem.realestatemanager.controller.fragment.DetailFragment
import kotlinx.android.synthetic.main.item_list_detail.view.*

class TestAdapter(val itemClick: (position:Int,item: DetailFragment.Item) -> Unit) : RecyclerView.Adapter<ItemViewHolder>() {

        private var items: List<DetailFragment.Item> = listOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_detail, parent, false))

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(items[position])
            holder.itemView.setOnClickListener {
                itemClick(position,items[position]) } }

        override fun getItemCount() = items.size

        fun setItems(newItems: List<DetailFragment.Item>) {
            items = newItems
            notifyDataSetChanged() } }

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: DetailFragment.Item) {
            view.list_item_text.text = item.mImageDescription
            view.list_item_icon.setImageURI(item.mImageUri.toUri()) } }