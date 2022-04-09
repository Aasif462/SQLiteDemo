package com.example.sqlitedemo.main.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sqlitedemo.R
import com.example.sqlitedemo.main.Model.ItemModel
import com.example.sqlitedemo.main.ui.MainActivity.Companion.ITEM_DETAILS
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.activity_main.*

class DetailsActivity : AppCompatActivity() {
    var counter = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        var itemModel:ItemModel ?= null

        if(intent.hasExtra(ITEM_DETAILS)){
            itemModel = intent.getParcelableExtra(MainActivity.ITEM_DETAILS)
        }

        if(itemModel != null){
            ItemImage.setImageURI(Uri.parse(itemModel.image))
            ItemName.text = itemModel.name
            ItemDescription.text = itemModel.description
            ItemPrice.text = itemModel.price
        }

        quantity()
        totalPrice.text = "Total Price : ${itemModel!!.price}"

        updateBtn.setOnClickListener {
            if(counter>1){
                val price = itemModel!!.price!!.toInt()
                val total = "${price*counter}"
                totalPrice.text = "Total Price : $total"
            }
            else{
                totalPrice.text = "Total Price : ${itemModel!!.price}"
            }

        }

        buyBtn.setOnClickListener {
            Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT).show()
        }
    }

    private fun quantity():Int{
        quantity.text = counter.toString()
        plusBtn.setOnClickListener {
            counter++
            quantity.text = counter.toString()
        }
        minusBtn.setOnClickListener {
            if(counter>1){
                counter--
                quantity.text = counter.toString()
            }

        }
        return counter
    }
}

