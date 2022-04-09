package com.example.sqlitedemo.main.Database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sqlitedemo.main.Model.ItemModel

class DBHelper(val context:Context):
    SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION) {

        companion object{
            private var DB_NAME="ItemDB"
            private var DB_VERSION=1
            private var TB_NAME="Items"
            private var ID="id"
            private var IMAGE="image"
            private var NAME="name"
            private var DESCRIPTION="age"
            private var PRICE="price"
        }

    override fun onCreate(p0: SQLiteDatabase?) {
        val query="CREATE TABLE $TB_NAME($ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $IMAGE TEXT , $NAME TEXT, $DESCRIPTION TEXT , $PRICE TEXT)"
        p0?.execSQL(query)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        var query="DROP TABLE $TB_NAME IF EXISTS"
        p0?.execSQL(query)
        onCreate(p0)

    }
    fun insert(p: ItemModel):Long
    {
        val db=writableDatabase
        val cv=ContentValues()
        cv.put(NAME,p.name)
        cv.put(DESCRIPTION,p.description)
        cv.put(IMAGE,p.image)
        cv.put(PRICE,p.price)
        val flag=db.insert(TB_NAME,null,cv)
        return flag
    }
    @SuppressLint("Recycle")
    fun retrieveAll():ArrayList<ItemModel>
    {
        val arr=ArrayList<ItemModel>()
        val db=readableDatabase
        val cursor=db.query(TB_NAME,null,null,null,null,null,null)
        while(cursor.moveToNext())
        {

            val id=cursor.getInt(0)
            val image = cursor.getString(1)
            val name=cursor.getString(2)
            val description=cursor.getString(3)
            val price=cursor.getString(4)
            val p=ItemModel(id,image,name, description,price)
            arr.add(p)
        }
        return arr

    }
    fun delete(id:Int)
    {
        val db=writableDatabase
        db.delete(TB_NAME,"$ID=$id",null)
        db.close()
    }
    fun update(p:ItemModel)
    {
        val db=writableDatabase
        val cv=ContentValues()
        cv.put(NAME,p.name)
        cv.put(DESCRIPTION,p.description)
        var flag=db.update(TB_NAME,cv,"$ID=${p.id}",
            null)
        db.close()
    }
}