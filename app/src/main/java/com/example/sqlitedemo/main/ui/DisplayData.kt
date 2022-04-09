package com.example.sqlitedemo.main.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sqlitedemo.R
import com.example.sqlitedemo.main.Adapter.ItemAdapter
import com.example.sqlitedemo.main.Database.DBHelper
import com.example.sqlitedemo.main.Model.ItemModel
import com.example.sqlitedemo.main.ui.MainActivity.Companion.ITEM_DETAILS
import kotlinx.android.synthetic.main.activity_display_data.*

class DisplayData : AppCompatActivity() {

    private lateinit var adapter:ItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_data)

        displayData()
    }
    private fun displayData()
    {
        val db= DBHelper(this)
        val arr:ArrayList<ItemModel> = db.retrieveAll()
        if(arr.size>=0){
            recView.visibility = View.VISIBLE
            textView2.visibility = View.GONE
        }
        adapter=ItemAdapter(this,arr)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView(){
        recView.adapter=adapter
        recView.layoutManager = LinearLayoutManager(this)
        adapter.setOnclickListener(object:ItemAdapter.OnclickListener{
            override fun onClick(position: Int, model: ItemModel) {
                val intent = Intent(this@DisplayData , DetailsActivity::class.java)
                intent.putExtra(ITEM_DETAILS , model)
                startActivity(intent)
            }
        })
    }

}