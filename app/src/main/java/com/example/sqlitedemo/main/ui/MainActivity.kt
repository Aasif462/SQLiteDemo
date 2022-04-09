package com.example.sqlitedemo.main.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.sqlitedemo.R
import com.example.sqlitedemo.main.Database.DBHelper
import com.example.sqlitedemo.main.Model.ItemModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() , View.OnClickListener {

    private val IMAGE_DIRECTORY = "SQLiteImages"
    private var saveImageToInternalStorage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            addImageTxt.setOnClickListener(this)
            saveBtn.setOnClickListener(this)
            viewBtn.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.addImageTxt -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select Photo From Gallery", "Capture Photo From Camara")
                dialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> choosePhotoFromCamara()
                    }
                }
                dialog.show()
            }

            R.id.saveBtn ->
            {
                insertData()
            }

            R.id.viewBtn ->
            {
                val intent = Intent(applicationContext , DisplayData::class.java)
                startActivity(intent)
            }
        }
    }

    private fun insertData(){
        val itemName = name.text.toString()
        val description = desc.text.toString()
        val itemPrice = price.text.toString()
        val image = saveImageToInternalStorage.toString()

        if(itemName.isEmpty() || description.isEmpty() || itemPrice.isEmpty() || saveImageToInternalStorage.toString().isEmpty()){
            Toast.makeText(applicationContext, "Please Enter Data", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(applicationContext, "Data Insert Successfully", Toast.LENGTH_SHORT)
                .show()

            val model = ItemModel(image,itemName,description,itemPrice)
            val db = DBHelper(this)
            val flag = db.insert(model)
            if(flag>0)
            {
                Toast.makeText(this,"Record Inserted!!",
                    Toast.LENGTH_LONG).show()
                placeImage.setImageResource(R.drawable.add_screen_image_placeholder)
                name.setText("")
                desc.setText("")
                price.setText("")
            }
        }
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        // Here this is used to get an bitmap from URI
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                        saveImageToInternalStorage =
                            saveImageToInternalStorage(selectedImageBitmap)
//                        Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                        placeImage!!.setImageBitmap(selectedImageBitmap) // Set the selected image from GALLERY to imageView.
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "Failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (requestCode == CAMERA) {

                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap // Bitmap from camera

                saveImageToInternalStorage =
                    saveImageToInternalStorage(thumbnail)
                Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                placeImage!!.setImageBitmap(thumbnail) // Set to the imageView.
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        /**
         * The Mode Private here is
         * File creation mode: the default mode, where the created file can only
         * be accessed by the calling application (or all applications sharing the
         * same user ID).
         */
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    // Here after all the permission are granted launch the gallery to select and image.
                    if (report!!.areAllPermissionsGranted()) {

                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(galleryIntent, GALLERY)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialog()
                }
            }).onSameThread()
            .check()
    }

    private fun showRationalDialog(){
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("It Looks Like You have turned off Permission required"+
                "For this Feature. It can be enabled Under the "+"Application Settings")
        dialog.setPositiveButton("GO TO SETTINGS"){
                dialog_,which ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",packageName ,null)
                intent.data = uri
                startActivity(intent)
            }
            catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }
        }
        dialog.setNegativeButton("Cancel"){
                dialog,_ ->
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun choosePhotoFromCamara() {

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Here after all the permission are granted launch the CAMERA to capture an image.
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialog()
                }
            }).onSameThread()
            .check()
    }

    companion object
    {
        const val ITEM_DETAILS = "item_details"
        const val CAMERA = 1
        const val GALLERY = 2
    }
}