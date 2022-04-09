package com.example.sqlitedemo.main.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlitedemo.R
import com.example.sqlitedemo.main.Model.ItemModel
import com.example.sqlitedemo.main.ui.DetailsActivity
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_layout.view.*

class ItemAdapter(context:Context,  val list:ArrayList<ItemModel>) :RecyclerView.Adapter<ItemAdapter.ItemHolder>() {

    private var onclickListener:OnclickListener ?= null

    inner class ItemHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.image
        val name:TextView = itemView.itemName
        val desc:TextView = itemView.itemDesc
        val price:TextView = itemView.itemPrice
        val purchase:Button = itemView.purchaseBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout , parent , false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val model = list[position]
        holder.image.setImageURI(Uri.parse(model.image))
        holder.name.text = model.name
        holder.desc.text = model.description
        holder.price.setText( "₹" + model.price)
        holder.purchase.setOnClickListener{
            if(onclickListener!=null){
                onclickListener!!.onClick(position , model)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnclickListener(onclickListener: OnclickListener){
        this.onclickListener = onclickListener
    }

    interface OnclickListener{
        fun onClick(position: Int , model: ItemModel)
    }
}